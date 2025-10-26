package ru.yandex.practicum.filmorate.storage.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Mpa> ratings = new HashMap<>();
    private int id = 0;

    @Override
    public Film createFilm(Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return films.values().stream().toList();
    }

    @Override
    public Film getFilmById(int filmId) {
        return films.get(filmId);
    }

    @Override
    public Film updateFilm(Film newFilm) {
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public List<Genre> getFilmGenres(int filmId) {
        return films.get(filmId).getGenres();
    }

    @Override
    public List<Genre> getAllGenres() {
        return InMemoryGenreData.getAllGenres();
    }

    @Override
    public Genre getGenreById(int genreId) {
        return InMemoryGenreData.getGenre(genreId);
    }

    @Override
    public List<Mpa> getRatings() {
        return InMemoryRatingData.getAllRatings();
    }

    @Override
    public Mpa getRatingById(int ratingId) {
        return InMemoryRatingData.getRating(ratingId);
    }

    @Override
    public List<Integer> getFilmLikes(int filmId) {
        return films.get(filmId).getLikes().stream().toList();
    }

    @Override
    public void addLike(int filmId, int userId) {
        films.get(filmId).getLikes().add(userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        films.get(filmId).getLikes().remove(userId);
    }

    @Override
    public List<Film> getMostPopular(int count) {
        return films.values().stream()
                .sorted(Comparator.comparing(Film::getLikesCount, Comparator.reverseOrder()))
                .limit(count)
                .toList();
    }
}
