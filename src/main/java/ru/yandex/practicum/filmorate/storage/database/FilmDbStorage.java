package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Primary
@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String FIND_ALL_FILMS_QUERY = "SELECT " +
            "f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME as RATING_NAME, g.GENRE_ID, g.NAME AS GENRE, fl.USER_ID AS \"LIKE\" " +
            "FROM FILM f LEFT JOIN RATING r ON f.rating_id = r.rating_id " +
            "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
            "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
            "LEFT JOIN FILM_LIKE fl ON f.FILM_ID = fl.FILM_ID";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT " +
            "f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME as RATING_NAME, g.GENRE_ID, g.NAME AS GENRE, fl.USER_ID AS \"LIKE\" " +
            "FROM FILM f LEFT JOIN RATING r ON f.rating_id = r.rating_id " +
            "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
            "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
            "LEFT JOIN FILM_LIKE fl ON f.FILM_ID = fl.FILM_ID " +
            "WHERE f.FILM_ID = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO FILM(name, description, release_date, duration, rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE FILM " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?";
    private static final String MOST_POPULAR_FILMS_QUERY = "SELECT " +
            "f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME as RATING_NAME, g.GENRE_ID, g.NAME AS GENRE, fl.USER_ID AS \"LIKE\" " +
            "FROM FILM f JOIN " +
            "(SELECT f1.film_id, count(user_id) AS likes_count FROM FILM f1 LEFT JOIN FILM_LIKE l1 using(film_id) GROUP BY f1.film_id ORDER BY count(user_id) DESC LIMIT ?) p " +
            "ON (f.film_id = p.film_id) " +
            "JOIN rating r ON f.rating_id = r.rating_id " +
            "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
            "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
            "LEFT JOIN FILM_LIKE fl ON f.FILM_ID = fl.FILM_ID " +
            "ORDER BY p.likes_count DESC";
    private static final String ALL_GENRES_QUERY =
            "SELECT * FROM GENRE";
    private static final String FIND_GENRE_BY_ID_QUERY =
            "SELECT * FROM GENRE WHERE genre_id = ?";
    private static final String DELETE_FILM_GENRES_QUERY =
            "DELETE FROM FILM_GENRE WHERE film_id = ?";
    private static final String ALL_RATINGS_QUERY =
            "SELECT * FROM RATING";
    private static final String FIND_RATING_BY_ID_QUERY =
            "SELECT rating_id, name FROM RATING WHERE rating_id = ?";
    private static final String FILM_LIKES_QUERY =
            "SELECT user_id FROM FILM_LIKE WHERE film_id = ?";
    private static final String ADD_LIKE_QUERY =
            "MERGE INTO FILM_LIKE (film_id, user_id) KEY (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY =
            "DELETE FROM FILM_LIKE where film_id = ? AND user_id = ?";
    private static final String ADD_MULTIPLE_GENRES_QUERY =
            "INSERT INTO FILM_GENRE(film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_FILM_QUERY =
            "DELETE FROM FILM WHERE film_id = ?";
    private static final String ADD_FILM_DIRECTORS_QUERY =
            "INSERT INTO film_director(film_id, director_id) VALUES (?, ?)";
    private static final String DELETE_FILM_DIRECTORS_QUERY =
            "DELETE FROM film_director WHERE film_id = ?";
    private static final String GET_FILM_DIRECTORS_QUERY =
            "SELECT d.* FROM directors d " +
                    "JOIN film_director fd ON d.director_id = fd.director_id " +
                    "WHERE fd.film_id = ?";
    private static final String GET_FILMS_BY_DIRECTOR_QUERY =
            "SELECT DISTINCT f.*, r.name as rating_name " +
                    "FROM film f " +
                    "JOIN rating r ON f.rating_id = r.rating_id " +
                    "JOIN film_director fd ON f.film_id = fd.film_id " +
                    "WHERE fd.director_id = ?";
    private final RowMapper<Genre> genreRowMapper;
    private final RowMapper<Mpa> mpaRowMapper;
    private RowMapper<Director> directorRowMapper;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper,
                         RowMapper<Genre> genreRowMapper, RowMapper<Mpa> mpaRowMapper,
                         RowMapper<Director> directorRowMapper) {
        super(jdbc, mapper);
        this.genreRowMapper = genreRowMapper;
        this.mpaRowMapper = mpaRowMapper;
        this.directorRowMapper = directorRowMapper;
    }

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

        // Добавляем жанры
        addFilmGenres(id, film.getGenres().stream().toList());

        // Добавляем режиссёров
        addFilmDirectors(id, film.getDirectors());

        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = groupValues(findMany(FIND_ALL_FILMS_QUERY));

        // Загружаем режиссёров для каждого фильма
        for (Film film : films) {
            List<Director> directors = getFilmDirectors(film.getId());
            film.setDirectors(new HashSet<>(directors));
        }

        return films;
    }

    @Override
    public Film getFilmById(int filmId) {
        List<Film> rawFilms = findMany(FIND_FILM_BY_ID_QUERY, filmId);
        if (rawFilms.isEmpty()) {
            return null;
        }
        Film film = groupValues(rawFilms).getFirst();

        // Загружаем режиссёров для фильма
        List<Director> directors = getFilmDirectors(filmId);
        film.setDirectors(new HashSet<>(directors));

        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        update(
                UPDATE_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                Date.valueOf(newFilm.getReleaseDate()),
                newFilm.getDuration(),
                newFilm.getMpa().getId()
        );

        // Обновляем жанры
        delete(DELETE_FILM_GENRES_QUERY, newFilm.getId());
        addFilmGenres(newFilm.getId(), newFilm.getGenres().stream().toList());

        // Обновляем режиссёров
        deleteFilmDirectors(newFilm.getId());
        addFilmDirectors(newFilm.getId(), newFilm.getDirectors());

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

    public void addFilmGenres(int filmId, List<Genre> genres) {
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
        return groupValues(rawFilms).stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .toList();
    }

    @Override
    public void addFilmDirectors(int filmId, Set<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return;
        }

        List<Director> directorList = new ArrayList<>(directors);
        jdbc.batchUpdate(ADD_FILM_DIRECTORS_QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Director director = directorList.get(i);
                ps.setInt(1, filmId);
                ps.setInt(2, director.getId());
            }

            @Override
            public int getBatchSize() {
                return directorList.size();
            }
        });
    }

    @Override
    public void deleteFilmDirectors(int filmId) {
        jdbc.update(DELETE_FILM_DIRECTORS_QUERY, filmId);
    }

    @Override
    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        // Получаем ID фильмов режиссёра
        String filmIdsQuery = "SELECT film_id FROM film_director WHERE director_id = ?";
        List<Integer> filmIds = jdbc.queryForList(filmIdsQuery, Integer.class, directorId);

        if (filmIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Получаем фильмы по ID
        List<Film> films = new ArrayList<>();
        for (Integer filmId : filmIds) {
            Film film = getFilmById(filmId);
            if (film != null) {
                films.add(film);
            }
        }

        // Сортируем фильмы
        if ("likes".equals(sortBy)) {
            films.sort((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()));
        } else if ("year".equals(sortBy)) {
            films.sort(Comparator.comparing(Film::getReleaseDate));
        }

        return films;
    }

    @Override
    public List<Director> getFilmDirectors(int filmId) {
        try {
            return jdbc.query(GET_FILM_DIRECTORS_QUERY, directorRowMapper, filmId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean deleteFilmById(int filmId) {
        int rowsAffected = jdbc.update(DELETE_FILM_QUERY, filmId);
        return rowsAffected > 0;
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
                return flm;
            });
        }
        return films.values().stream().toList();
    }
}
