package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Set;

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

    void addFilmDirectors(int filmId, Set<Director> directors);

    void deleteFilmDirectors(int filmId);

    List<Film> getFilmsByDirector(int directorId, String sortBy);

    List<Director> getFilmDirectors(int filmId);

    List<Film> searchFilms(String query, boolean searchByTitle, boolean searchByDirector);
}
