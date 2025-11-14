package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(int filmId);

    Film updateFilm(Film newFilm);

    List<Genre> getAllGenres();

    Genre getGenreById(int genreId);

    List<Mpa> getRatings();

    Mpa getRatingById(int ratingId);

    List<Integer> getFilmLikes(int filmId);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    boolean deleteFilmById(int filmId);

    List<Film> getMostPopular(int count);

    List<Film> getPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year);

    List<Film> getCommonFilms(int userId, int friendId);
}
