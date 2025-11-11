package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String FIND_ALL_FILMS_QUERY = "SELECT " +
            "f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
            "f.RATING_ID, r.NAME as RATING_NAME, g.GENRE_ID, g.NAME AS GENRE, " +
            "fl.USER_ID AS \"LIKE\", d.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
            "FROM FILM f " +
            "LEFT JOIN RATING r ON f.rating_id = r.rating_id " +
            "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
            "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
            "LEFT JOIN FILM_LIKE fl ON f.FILM_ID = fl.FILM_ID " +
            "LEFT JOIN FILM_DIRECTOR fd ON f.FILM_ID = fd.FILM_ID " +
            "LEFT JOIN DIRECTOR d ON fd.DIRECTOR_ID = d.DIRECTOR_ID";

    private static final String FIND_FILM_BY_ID_QUERY = "SELECT " +
            "f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
            "f.RATING_ID, r.NAME as RATING_NAME, g.GENRE_ID, g.NAME AS GENRE, " +
            "fl.USER_ID AS \"LIKE\", d.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
            "FROM FILM f " +
            "LEFT JOIN RATING r ON f.rating_id = r.rating_id " +
            "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
            "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
            "LEFT JOIN FILM_LIKE fl ON f.FILM_ID = fl.FILM_ID " +
            "LEFT JOIN FILM_DIRECTOR fd ON f.FILM_ID = fd.FILM_ID " +
            "LEFT JOIN DIRECTOR d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
            "WHERE f.FILM_ID = ?";

    private static final String INSERT_FILM_QUERY = "INSERT INTO FILM(name, description, release_date, duration, rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_FILM_QUERY = "UPDATE FILM " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
            "WHERE film_id = ?";

    private static final String MOST_POPULAR_FILMS_QUERY = "SELECT " +
            "f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
            "f.RATING_ID, r.NAME as RATING_NAME, g.GENRE_ID, g.NAME AS GENRE, " +
            "fl.USER_ID AS \"LIKE\", d.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
            "FROM FILM f JOIN " +
            "(SELECT film_id, count(user_id) AS likes_count FROM FILM_LIKE GROUP BY film_id ORDER BY count(user_id) DESC, film_id LIMIT ?) p " +
            "ON (f.film_id = p.film_id) " +
            "JOIN rating r ON f.rating_id = r.rating_id " +
            "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
            "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
            "LEFT JOIN FILM_LIKE fl ON f.FILM_ID = fl.FILM_ID " +
            "LEFT JOIN FILM_DIRECTOR fd ON f.FILM_ID = fd.FILM_ID " +
            "LEFT JOIN DIRECTOR d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
            "ORDER BY p.likes_count DESC";

    private static final String SEARCH_FILMS_QUERY = "SELECT " +
            "f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
            "f.RATING_ID, r.NAME as RATING_NAME, g.GENRE_ID, g.NAME AS GENRE, " +
            "fl.USER_ID AS \"LIKE\", d.DIRECTOR_ID, d.NAME AS DIRECTOR_NAME " +
            "FROM FILM f " +
            "LEFT JOIN RATING r ON f.rating_id = r.rating_id " +
            "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID " +
            "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
            "LEFT JOIN FILM_LIKE fl ON f.FILM_ID = fl.FILM_ID " +
            "LEFT JOIN FILM_DIRECTOR fd ON f.FILM_ID = fd.FILM_ID " +
            "LEFT JOIN DIRECTOR d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
            "WHERE 1=1";

    private static final String ALL_GENRES_QUERY = "SELECT * FROM GENRE";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM GENRE WHERE genre_id = ?";
    private static final String ALL_RATINGS_QUERY = "SELECT * FROM RATING";
    private static final String FIND_RATING_BY_ID_QUERY = "SELECT rating_id, name FROM RATING WHERE rating_id = ?";
    private static final String FILM_LIKES_QUERY = "SELECT user_id FROM FILM_LIKE WHERE film_id = ?";
    private static final String ADD_LIKE_QUERY = "MERGE INTO FILM_LIKE (film_id, user_id) KEY (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM FILM_LIKE where film_id = ? AND user_id = ?";
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM FILM_GENRE WHERE film_id = ?";
    private static final String ADD_FILM_GENRE_QUERY = "INSERT INTO FILM_GENRE(film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_FILM_DIRECTORS_QUERY = "DELETE FROM FILM_DIRECTOR WHERE film_id = ?";
    private static final String ADD_FILM_DIRECTOR_QUERY = "INSERT INTO FILM_DIRECTOR(film_id, director_id) VALUES (?, ?)";

    private final JdbcTemplate jdbc;
    private final RowMapper<Film> filmRowMapper;
    private final RowMapper<Genre> genreRowMapper;
    private final RowMapper<Mpa> mpaRowMapper;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> filmRowMapper,
                         RowMapper<Genre> genreRowMapper, RowMapper<Mpa> mpaRowMapper) {
        super(jdbc, filmRowMapper);
        this.jdbc = jdbc;
        this.filmRowMapper = filmRowMapper;
        this.genreRowMapper = genreRowMapper;
        this.mpaRowMapper = mpaRowMapper;
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

        // Сохраняем жанры
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            addFilmGenres(id, film.getGenres().stream().toList());
        }

        // Сохраняем режиссеров
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            addFilmDirectors(id, film.getDirectors().stream().toList());
        }

        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> rawFilms = findMany(FIND_ALL_FILMS_QUERY);
        return groupFilmValues(rawFilms);
    }

    @Override
    public Film getFilmById(int filmId) {
        List<Film> rawFilms = findMany(FIND_FILM_BY_ID_QUERY, filmId);
        if (rawFilms.isEmpty()) {
            return null;
        }
        return groupFilmValues(rawFilms).getFirst();
    }

    @Override
    public Film updateFilm(Film newFilm) {
        update(
                UPDATE_FILM_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                Date.valueOf(newFilm.getReleaseDate()),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );

        // Обновляем жанры
        delete(DELETE_FILM_GENRES_QUERY, newFilm.getId());
        if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
            addFilmGenres(newFilm.getId(), newFilm.getGenres().stream().toList());
        }

        // Обновляем режиссеров
        delete(DELETE_FILM_DIRECTORS_QUERY, newFilm.getId());
        if (newFilm.getDirectors() != null && !newFilm.getDirectors().isEmpty()) {
            addFilmDirectors(newFilm.getId(), newFilm.getDirectors().stream().toList());
        }

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
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public List<Film> getMostPopular(int count) {
        List<Film> rawFilms = findMany(MOST_POPULAR_FILMS_QUERY, count);
        return groupFilmValues(rawFilms).stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .toList();
    }

    @Override
    public List<Film> searchFilms(String query, List<String> searchBy) {
        StringBuilder sqlBuilder = new StringBuilder(SEARCH_FILMS_QUERY);
        String searchQuery = "%" + query.toLowerCase() + "%";

        if (searchBy.contains("title") && searchBy.contains("director")) {
            sqlBuilder.append(" AND (LOWER(f.NAME) LIKE ? OR LOWER(d.NAME) LIKE ?)");
            List<Film> rawFilms = findMany(sqlBuilder.toString(), searchQuery, searchQuery);
            return groupFilmValues(rawFilms);
        } else if (searchBy.contains("title")) {
            sqlBuilder.append(" AND LOWER(f.NAME) LIKE ?");
            List<Film> rawFilms = findMany(sqlBuilder.toString(), searchQuery);
            return groupFilmValues(rawFilms);
        } else if (searchBy.contains("director")) {
            sqlBuilder.append(" AND LOWER(d.NAME) LIKE ?");
            List<Film> rawFilms = findMany(sqlBuilder.toString(), searchQuery);
            return groupFilmValues(rawFilms);
        }

        return List.of();
    }

    private void addFilmGenres(int filmId, List<Genre> genres) {
        jdbc.batchUpdate(ADD_FILM_GENRE_QUERY, new BatchPreparedStatementSetter() {
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

    private void addFilmDirectors(int filmId, List<Director> directors) {
        jdbc.batchUpdate(ADD_FILM_DIRECTOR_QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Director director = directors.get(i);
                ps.setInt(1, filmId);
                ps.setInt(2, director.getId());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        });
    }

    private List<Film> groupFilmValues(List<Film> rawFilms) {
        Map<Integer, Film> films = new HashMap<>();
        for (Film film : rawFilms) {
            films.compute(film.getId(), (id, existingFilm) -> {
                if (existingFilm == null) {
                    return film;
                }
                // Добавляем жанры
                if (!film.getGenres().isEmpty()) {
                    film.getGenres().forEach(genre -> {
                        if (!existingFilm.getGenres().contains(genre)) {
                            existingFilm.getGenres().add(genre);
                        }
                    });
                }
                // Добавляем лайки
                if (!film.getLikes().isEmpty()) {
                    film.getLikes().forEach(like -> {
                        if (!existingFilm.getLikes().contains(like)) {
                            existingFilm.getLikes().add(like);
                        }
                    });
                }
                // Добавляем режиссеров
                if (!film.getDirectors().isEmpty()) {
                    film.getDirectors().forEach(director -> {
                        if (!existingFilm.getDirectors().contains(director)) {
                            existingFilm.getDirectors().add(director);
                        }
                    });
                }
                return existingFilm;
            });
        }
        return films.values().stream().toList();
    }

    @Override
    public List<Film> getDirectorFilms(int directorId, String sortBy) {
        StringBuilder sqlBuilder = new StringBuilder(FIND_ALL_FILMS_QUERY);
        sqlBuilder.append(" WHERE d.DIRECTOR_ID = ?");

        // Добавляем сортировку
        if ("year".equals(sortBy)) {
            sqlBuilder.append(" ORDER BY f.RELEASE_DATE");
        } else if ("likes".equals(sortBy)) {
            sqlBuilder.append(" ORDER BY (SELECT COUNT(*) FROM FILM_LIKE fl2 WHERE fl2.FILM_ID = f.FILM_ID) DESC");
        } else {
            sqlBuilder.append(" ORDER BY f.FILM_ID"); // сортировка по умолчанию
        }

        List<Film> rawFilms = findMany(sqlBuilder.toString(), directorId);
        return groupFilmValues(rawFilms);
    }
}
