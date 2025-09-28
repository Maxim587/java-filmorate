package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private int id = 0;

    public Film create(Film film) {
        film.setId(++id);
        return filmStorage.addFilm(film);
    }

    public Collection<Film> findAll() {
        return filmStorage.getFilms();
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.info("Film updating failed: id not provided");
            throw new ValidationException("Id должен быть указан");
        }

        Film oldFilm = filmStorage.getFilmById(newFilm.getId());
        if (oldFilm == null) {
            log.info("Film updating failed: film with id:{} not found", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        return oldFilm;
    }

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Ошибка добавления лайка к фильму. Фильм не найден");
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Ошибка добавления лайка к фильму. Пользователь не найден");
        }

        film.addLike(user);
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Ошибка удаления лайка к фильму. Фильм не найден");
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Ошибка удаления лайка к фильму. Пользователь не найден");
        }

        film.deleteLike(user);
    }

    public Collection<Film> getMostPopular(int count) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparing(Film::getLikesCount, Comparator.reverseOrder()))
                .limit(count)
                .toList();
    }

    public Optional<Film> findById(int id) {
        return Optional.ofNullable(filmStorage.getFilmById(id));
    }
}
