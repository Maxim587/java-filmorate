package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Primary
@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final RowMapper<Film> filmRowMapper;
    private final RowMapper<Genre> genreRowMapper;
    private final RowMapper<Mpa> mpaRowMapper;

    public FilmDbStorage(JdbcTemplate jdbc,
                         RowMapper<Film> filmRowMapper,
                         RowMapper<Genre> genreRowMapper,
                         RowMapper<Mpa> mpaRowMapper) {
        super(jdbc, filmRowMapper);
        this.jdbc = jdbc;
        this.filmRowMapper = filmRowMapper;
        this.genreRowMapper = genreRowMapper;
        this.mpaRowMapper = mpaRowMapper;
    }

    // Все остальные методы остаются без изменений...
    private static final String FIND_ALL_FILMS_QUERY = "SELECT " +
            "f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME as RATING_NAME, " +
            "g.GENRE_ID, g.NAME AS GENRE, fl.USER_ID AS \"LIKE\", " +
            "d.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
            "FROM FILM f LEFT JOIN RATING r ON f.rating_id = r.rating_id " +
            "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
            "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
            "LEFT JOIN FILM_LIKE fl ON f.FILM_ID = fl.FILM_ID " +
            "LEFT JOIN FILM_DIRECTOR fd ON f.FILM_ID = fd.FILM_ID " +
            "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID";

    private static final String FIND_FILM_BY_ID_QUERY = "SELECT " +
            "f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME as RATING_NAME, " +
            "g.GENRE_ID, g.NAME AS GENRE, fl.USER_ID AS \"LIKE\", " +
            "d.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
            "FROM FILM f LEFT JOIN RATING r ON f.rating_id = r.rating_id " +
            "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
            "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
            "LEFT JOIN FILM_LIKE fl ON f.FILM_ID = fl.FILM_ID " +
            "LEFT JOIN FILM_DIRECTOR fd ON f.FILM_ID = fd.FILM_ID " +
            "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
            "WHERE f.FILM_ID = ?";

    private static final String INSERT_FILM_QUERY = "INSERT INTO FILM(name, description, release_date, duration, rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_QUERY = "UPDATE FILM " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";

    private static final String MOST_POPULAR_FILMS_QUERY = "SELECT " +
            "f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME as RATING_NAME, " +
            "g.GENRE_ID, g.NAME AS GENRE, fl.USER_ID AS \"LIKE\", " +
            "d.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
            "FROM FILM f JOIN " +
            "(SELECT film_id, count(user_id) AS likes_count FROM FILM_LIKE GROUP BY film_id ORDER BY count(user_id) DESC, film_id LIMIT ?) p " +
            "ON (f.film_id = p.film_id) " +
            "JOIN rating r ON f.rating_id = r.rating_id " +
            "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
            "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
            "LEFT JOIN FILM_LIKE fl ON f.FILM_ID = fl.FILM_ID " +
            "LEFT JOIN FILM_DIRECTOR fd ON f.FILM_ID = fd.FILM_ID " +
            "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
            "ORDER BY p.likes_count DESC";

    private static final String ALL_GENRES_QUERY = "SELECT * FROM GENRE";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM GENRE WHERE genre_id = ?";
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM FILM_GENRE WHERE film_id = ?";
    private static final String ALL_RATINGS_QUERY = "SELECT * FROM RATING";
    private static final String FIND_RATING_BY_ID_QUERY = "SELECT rating_id, name FROM RATING WHERE rating_id = ?";
    private static final String FILM_LIKES_QUERY = "SELECT user_id FROM FILM_LIKE WHERE film_id = ?";
    private static final String ADD_LIKE_QUERY = "MERGE INTO FILM_LIKE (film_id, user_id) KEY (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM FILM_LIKE where film_id = ? AND user_id = ?";
    private static final String ADD_MULTIPLE_GENRES_QUERY = "INSERT INTO FILM_GENRE(film_id, genre_id) VALUES (?, ?)";

    private static final String SEARCH_FILMS_BY_TITLE_QUERY = FIND_ALL_FILMS_QUERY +
            " WHERE LOWER(f.NAME) LIKE LOWER(?)";

    private static final String SEARCH_FILMS_BY_DIRECTOR_QUERY = FIND_ALL_FILMS_QUERY +
            " WHERE LOWER(d.NAME) LIKE LOWER(?)";

    private static final String SEARCH_FILMS_BY_TITLE_AND_DIRECTOR_QUERY = FIND_ALL_FILMS_QUERY +
            " WHERE (LOWER(f.NAME) LIKE LOWER(?) OR LOWER(d.NAME) LIKE LOWER(?))";

    private static final String FILMS_BY_DIRECTOR_QUERY = FIND_ALL_FILMS_QUERY +
            " WHERE d.DIRECTOR_ID = ?";

    private static final String FILMS_BY_DIRECTOR_ORDER_BY_LIKES = FILMS_BY_DIRECTOR_QUERY +
            " ORDER BY (SELECT COUNT(*) FROM FILM_LIKE fl WHERE fl.FILM_ID = f.FILM_ID) DESC";

    private static final String FILMS_BY_DIRECTOR_ORDER_BY_YEAR = FILMS_BY_DIRECTOR_QUERY +
            " ORDER BY f.RELEASE_DATE";

    private static final String DELETE_FILM_DIRECTORS_QUERY = "DELETE FROM FILM_DIRECTOR WHERE film_id = ?";
    private static final String ADD_MULTIPLE_DIRECTORS_QUERY = "INSERT INTO FILM_DIRECTOR(film_id, director_id) VALUES (?, ?)";

    @Override
    public Film createFilm(Film film) {
        int id = insert(
                INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);

        addFilmGenres(id, new ArrayList<>(film.getGenres()));
        addFilmDirectors(id, new ArrayList<>(film.getDirectors()));

        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> rawFilms = findMany(FIND_ALL_FILMS_QUERY);
        if (rawFilms.isEmpty()) {
            return rawFilms;
        }
        return groupValues(rawFilms);
    }

    @Override
    public Film getFilmById(int filmId) {
        List<Film> rawFilms = findMany(FIND_FILM_BY_ID_QUERY, filmId);
        if (rawFilms.isEmpty()) {
            return null;
        }
        return groupValues(rawFilms).getFirst();
    }

    @Override
    public Film updateFilm(Film newFilm) {
        update(
                UPDATE_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                Date.valueOf(newFilm.getReleaseDate()),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );

        delete(DELETE_FILM_GENRES_QUERY, newFilm.getId());
        addFilmGenres(newFilm.getId(), new ArrayList<>(newFilm.getGenres()));

        delete(DELETE_FILM_DIRECTORS_QUERY, newFilm.getId());
        addFilmDirectors(newFilm.getId(), new ArrayList<>(newFilm.getDirectors()));

        return newFilm;
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbc.query(ALL_GENRES_QUERY, genreRowMapper);
    }

    @Override
    public Genre getGenreById(int genreId) {
        try {
            return jdbc.queryForObject(FIND_GENRE_BY_ID_QUERY, genreRowMapper, genreId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    @Override
    public List<Mpa> getRatings() {
        return jdbc.query(ALL_RATINGS_QUERY, mpaRowMapper);
    }

    @Override
    public Mpa getRatingById(int ratingId) {
        try {
            return jdbc.queryForObject(FIND_RATING_BY_ID_QUERY, mpaRowMapper, ratingId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    @Override
    public List<Integer> getFilmLikes(int filmId) {
        return jdbc.queryForList(FILM_LIKES_QUERY, Integer.class, filmId);
    }

    @Override
    public void addLike(int filmId, int userId) {
        update(ADD_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        update(DELETE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public List<Film> getMostPopular(int count) {
        List<Film> rawFilms = findMany(MOST_POPULAR_FILMS_QUERY, count);
        if (rawFilms.isEmpty()) {
            return rawFilms;
        }

        List<Film> groupedFilms = groupValues(rawFilms);

        // Сортируем по количеству лайков в порядке убывания
        return groupedFilms.stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikesCount(), f1.getLikesCount()))
                .toList();
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        String searchPattern = "%" + query + "%";

        if (by.equals("title")) {
            return groupValues(findMany(SEARCH_FILMS_BY_TITLE_QUERY, searchPattern));
        } else if (by.equals("director")) {
            return groupValues(findMany(SEARCH_FILMS_BY_DIRECTOR_QUERY, searchPattern));
        } else if (by.equals("title,director") || by.equals("director,title")) {
            return groupValues(findMany(SEARCH_FILMS_BY_TITLE_AND_DIRECTOR_QUERY, searchPattern, searchPattern));
        } else {
            throw new IllegalArgumentException("Invalid search parameter: " + by);
        }
    }

    @Override
    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        List<Film> films;
        if ("year".equals(sortBy)) {
            films = findMany(FILMS_BY_DIRECTOR_ORDER_BY_YEAR, directorId);
        } else if ("likes".equals(sortBy)) {
            films = findMany(FILMS_BY_DIRECTOR_ORDER_BY_LIKES, directorId);
        } else {
            films = findMany(FILMS_BY_DIRECTOR_QUERY, directorId);
        }
        return groupValues(films);
    }

    private void addFilmGenres(int filmId, List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }

        jdbc.batchUpdate(ADD_MULTIPLE_GENRES_QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = genres.get(i);
                ps.setInt(1, filmId);
                ps.setInt(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    private void addFilmDirectors(int filmId, List<ru.yandex.practicum.filmorate.model.Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return;
        }

        jdbc.batchUpdate(ADD_MULTIPLE_DIRECTORS_QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ru.yandex.practicum.filmorate.model.Director director = directors.get(i);
                ps.setInt(1, filmId);
                ps.setInt(2, director.getId());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        });
    }

    private List<Film> groupValues(List<Film> rawFilms) {
        Map<Integer, Film> films = new HashMap<>();
        for (Film film : rawFilms) {
            films.compute(film.getId(), (id, flm) -> {
                if (flm == null) {
                    return film;
                }
                if (!film.getGenres().isEmpty()) {
                    flm.getGenres().addAll(film.getGenres());
                }
                if (!film.getLikes().isEmpty()) {
                    flm.getLikes().addAll(film.getLikes());
                }
                if (!film.getDirectors().isEmpty()) {
                    flm.getDirectors().addAll(film.getDirectors());
                }
                return flm;
            });
        }
        return new ArrayList<>(films.values());
    }
}
