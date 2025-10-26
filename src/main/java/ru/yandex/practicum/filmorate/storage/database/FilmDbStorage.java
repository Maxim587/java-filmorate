package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.database.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.database.mapper.MpaRowMapper;

import java.sql.Date;
import java.util.List;

@Primary
@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String FIND_ALL_FILMS_QUERY = "SELECT f.*, r.name as rating_name " +
            "FROM FILM f " +
            "LEFT JOIN RATING r ON f.rating_id = r.rating_id";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT f.*, r.name as rating_name " +
            "FROM FILM f " +
            "LEFT JOIN RATING r ON f.rating_id = r.rating_id " +
            "WHERE film_id = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO FILM(name, description, release_date, duration, rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE FILM " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?";
    private static final String MOST_POPULAR_FILMS_QUERY = "SELECT f.*, r.name as rating_name " +
            "FROM FILM f JOIN " +
            "(SELECT film_id, count(user_id) AS likes_count FROM FILM_LIKE GROUP BY film_id ORDER BY count(user_id) DESC, film_id LIMIT ?) p " +
            "ON (f.film_id = p.film_id) " +
            "JOIN RATING r ON (f.rating_id = r.rating_id) " +
            "ORDER BY p.likes_count DESC";
    private static final String ADD_GENRE_QUERY =
            "INSERT INTO FILM_GENRE(film_id, genre_id) VALUES (?, ?)";
    private static final String FILM_GENRES_QUERY =
            "SELECT g.genre_id, g.name FROM FILM_GENRE fg " +
                    "JOIN GENRE g ON fg.genre_id = g.genre_id WHERE film_id = ?";
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

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
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

        for (Genre genre : film.getGenres()) {
            jdbc.update(ADD_GENRE_QUERY, id, genre.getId());
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return findMany(FIND_ALL_FILMS_QUERY);
    }

    @Override
    public Film getFilmById(int filmId) {
        return findOne(FIND_FILM_BY_ID_QUERY, filmId);
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

        if (newFilm.getGenres() != null) {
            delete(DELETE_FILM_GENRES_QUERY, newFilm.getId());

            for (Genre genre : newFilm.getGenres()) {
                jdbc.update(ADD_GENRE_QUERY, newFilm.getId(), genre.getId());
            }
        }

        return newFilm;
    }

    @Override
    public List<Genre> getFilmGenres(int filmId) {
        return jdbc.query(FILM_GENRES_QUERY, new GenreRowMapper(), filmId);
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbc.query(ALL_GENRES_QUERY, new GenreRowMapper());
    }

    @Override
    public Genre getGenreById(int genreId) {
        try {
            return jdbc.queryForObject(FIND_GENRE_BY_ID_QUERY, new GenreRowMapper(), genreId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    @Override
    public List<Mpa> getRatings() {
        return jdbc.query(ALL_RATINGS_QUERY, new MpaRowMapper());
    }

    @Override
    public Mpa getRatingById(int ratingId) {
        try {
            return jdbc.queryForObject(FIND_RATING_BY_ID_QUERY, new MpaRowMapper(), ratingId);
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
        return findMany(MOST_POPULAR_FILMS_QUERY, count);
    }
}
