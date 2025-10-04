package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film addFilm(Film film);

    Collection<Film> getFilms();

    Film getFilmById(int filmId);

    Film update(Film newFilm);
}
