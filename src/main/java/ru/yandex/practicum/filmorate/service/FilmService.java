package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmDto createFilm(NewFilmDto newFilmDto) {
        Mpa mpa = Optional.ofNullable(filmStorage.getRatingById(newFilmDto.getMpa().getId()))
                .orElseThrow(() -> {
                    log.info("Rating not exists. Error while creating film {}", newFilmDto);
                    return new NotFoundException("Рейтинг не существует id: " + newFilmDto.getMpa().getId());
                });

        List<Genre> genres = new ArrayList<>();
        if (newFilmDto.getGenres() != null) {
            genres = newFilmDto.getGenres().stream()
                    .distinct()
                    .map(GenreRequestDto::getId)
                    .map(id -> {
                        Optional<Genre> genre = Optional.ofNullable(filmStorage.getGenreById(id));
                        return genre.orElseThrow(() -> {
                            log.info("Genre not exists. Error while creating film {}", newFilmDto);
                            return new NotFoundException("Жанр не существует id: " + id);
                        });
                    })
                    .toList();
        }

        Film film = FilmMapper.mapToFilm(newFilmDto, mpa, genres);
        film = filmStorage.createFilm(film);
        return FilmMapper.mapToFilmDto(film);
    }

    public List<FilmDto> getAllFilms() {
        return filmStorage.getAllFilms().stream()
                .map(film -> {
                    List<Genre> genres = filmStorage.getFilmGenres(film.getId());
                    List<Integer> likes = filmStorage.getFilmLikes(film.getId());
                    film.setGenres(genres);
                    film.setLikes(new HashSet<>(likes));
                    return FilmMapper.mapToFilmDto(film);
                })
                .toList();
    }

    public FilmDto getFilmById(int id) {
        Film film = Optional.ofNullable(filmStorage.getFilmById(id))
                .orElseThrow(() -> {
                    log.info("Error while getting film by id. Film not found id: {}", id);
                    return new NotFoundException("Фильм с id:" + id + " не найден");
                });

        List<Genre> genres = filmStorage.getFilmGenres(film.getId());
        List<Integer> likes = filmStorage.getFilmLikes(film.getId());
        film.setGenres(genres);
        film.setLikes(new HashSet<>(likes));
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(UpdateFilmDto newFilm) {
        if (newFilm.getId() == null) {
            log.info("Film updating failed: id not provided {}", newFilm);
            throw new ValidationException("Id должен быть указан");
        }

        Film oldFilm = filmStorage.getFilmById(newFilm.getId());
        if (oldFilm == null) {
            log.info("Film updating failed: film with id:{} not found", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        if (newFilm.getMpa().getId() != oldFilm.getMpa().getId()) {
            oldFilm.setMpa(filmStorage.getRatingById(newFilm.getMpa().getId()));
        }

        FilmMapper.updateFilmFields(oldFilm, newFilm);

        if (newFilm.getGenres() != null) {
            List<Integer> newFilmGenreIds = newFilm.getGenres().stream()
                    .map(GenreRequestDto::getId)
                    .distinct()
                    .toList();
            oldFilm.setGenres(newFilmGenreIds.stream().map(filmStorage::getGenreById).toList());
        }
        return FilmMapper.mapToFilmDto(filmStorage.updateFilm(oldFilm));
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        Genre genre = filmStorage.getGenreById(id);
        if (genre == null) {
            log.info("Error while getting genre by id. Genre not found id: {}", id);
            throw new NotFoundException("Жанр не найден id = " + id);
        }
        return genre;
    }

    public List<Mpa> getMpaList() {
        return filmStorage.getRatings();
    }

    public Mpa getMpaById(int id) {
        Mpa mpa = filmStorage.getRatingById(id);
        if (mpa == null) {
            log.info("Error while getting rating by id. Rating not found id: {}", id);
            throw new NotFoundException("Рейтинг не найден id = " + id);
        }
        return mpa;
    }

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.info("Error while adding like. Film not found id: {}", filmId);
            throw new NotFoundException("Ошибка добавления лайка к фильму. Фильм не найден");
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.info("Error while adding like. User not found id: {}", userId);
            throw new NotFoundException("Ошибка добавления лайка к фильму. Пользователь не найден");
        }

        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.info("Error while deleting like. Film not found id: {}", filmId);
            throw new NotFoundException("Ошибка удаления лайка к фильму. Фильм не найден");
        }

        List<Integer> likes = filmStorage.getFilmLikes(filmId);
        if (likes == null || likes.isEmpty() || !likes.contains(userId)) {
            log.info("Error while deleting like. Like not found.");
            throw new NotFoundException("Ошибка удаления лайка к фильму. Лайк не найден");
        }

        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getMostPopular(int count) {
        return filmStorage.getMostPopular(count);
    }
}
