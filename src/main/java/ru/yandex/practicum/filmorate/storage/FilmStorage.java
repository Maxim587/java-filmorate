package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    List<Film> getAllFilms();

    List<Film> getUserLikedFilms(int userId);

    List<Film> getRecommended(int toUserId, int fromUserId);

    Film getFilmById(int filmId);

    Film updateFilm(Film newFilm);

    List<Genre> getAllGenres();

    Genre getGenreById(int genreId);

    List<Genre> getGenresByIds(List<Integer> genresIds);

    List<Mpa> getRatings();

    Mpa getRatingById(int ratingId);

    List<Integer> getFilmLikes(int filmId);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    boolean deleteFilmById(int filmId);

    List<Film> getMostPopular(int count);

    void addFilmDirectors(int filmId, List<Director> directors);

    List<Film> getFilmsByDirector(int directorId, String sortBy);

    List<Director> getFilmDirectors(int filmId);

    List<Film> getPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year);

    List<Film> getCommonFilms(int userId, int friendId);

    List<Film> searchFilms(String query, boolean searchByTitle, boolean searchByDirector);

    List<Film> getRecommendedAlt(int userId);

    List<Director> getDirectorsByIds(List<Integer> directorIds);
}
