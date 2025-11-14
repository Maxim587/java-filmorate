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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


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

        Film film = FilmMapper.mapToFilm(newFilmDto);
        film.setMpa(mpa);

        if (newFilmDto.getGenres() != null && !newFilmDto.getGenres().isEmpty()) {
            film.setGenres(mapFilmGenres(newFilmDto.getGenres()));
        }

        return FilmMapper.mapToFilmDto(filmStorage.createFilm(film));
    }

    public List<FilmDto> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        if (films.isEmpty()) {
            return Collections.emptyList();
        }
        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto getFilmById(int id) {
        return Optional.ofNullable(filmStorage.getFilmById(id))
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> {
                    log.info("Error while getting film by id. Film not found id: {}", id);
                    return new NotFoundException("Фильм с id:" + id + " не найден");
                });
    }

    public FilmDto updateFilm(UpdateFilmDto newFilm) {
        if (newFilm.getId() == null) {
            log.info("Film updating failed: id not provided {}", newFilm);
            throw new ValidationException("Id должен быть указан");
        }

        Film filmToUpdate = filmStorage.getFilmById(newFilm.getId());
        if (filmToUpdate == null) {
            log.info("Film updating failed: film with id:{} not found", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        if (newFilm.getMpa().getId() != filmToUpdate.getMpa().getId()) {
            filmToUpdate.setMpa(filmStorage.getRatingById(newFilm.getMpa().getId()));
        }

        FilmMapper.updateFilmFields(filmToUpdate, newFilm);

        if (newFilm.getGenres() != null) {
            filmToUpdate.setGenres(mapFilmGenres(newFilm.getGenres()));
        }

        return FilmMapper.mapToFilmDto(filmStorage.updateFilm(filmToUpdate));
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return Optional.ofNullable(filmStorage.getGenreById(id)).orElseThrow(() -> {
            log.info("Error while getting genre by id. Genre not found id: {}", id);
            return new NotFoundException("Жанр не найден id = " + id);
        });
    }

    public List<Mpa> getMpaList() {
        return filmStorage.getRatings();
    }

    public Mpa getMpaById(int id) {
        return Optional.ofNullable(filmStorage.getRatingById(id)).orElseThrow(() -> {
            log.info("Error while getting mpa by id. Mpa not found id: {}", id);
            return new NotFoundException("Рейтинг не найден id = " + id);
        });
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

    public List<FilmDto> getMostPopular(int count) {
        List<Film> films = filmStorage.getMostPopular(count);
        if (films.isEmpty()) {
            return Collections.emptyList();
        }
        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    private Set<Genre> mapFilmGenres(Set<GenreRequestDto> filmRequestGenres) {
        if (filmRequestGenres.isEmpty()) {
            return Collections.emptySet();
        }
        Map<Integer, Genre> genresFromDb = filmStorage.getAllGenres().stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity()));

        return filmRequestGenres.stream()
                .map(genre -> Optional.ofNullable(genresFromDb.get(genre.getId())).orElseThrow(() -> {
                    log.info("Genre not exists: {}", genre);
                    return new NotFoundException("Жанр не существует id: " + genre.getId());
                }))
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public boolean deleteFilmById(int filmId) {
        boolean deleted = filmStorage.deleteFilmById(filmId);
        if (!deleted) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
        return true;
    }

    public List<FilmDto> getPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year) {
        List<Film> films = filmStorage.getPopularFilmsByGenreAndYear(count, genreId, year);
        if (films.isEmpty()) {
            return Collections.emptyList();
        }
        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public List<FilmDto> getCommonFilms(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.info("Error while getting common films. User not found id: {}", userId);
            throw new NotFoundException("Пользователь с id:" + userId + " не найден");
        }

        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            log.info("Error while getting common films. Friend not found id: {}", friendId);
            throw new NotFoundException("Пользователь с id:" + friendId + " не найден");
        }

        List<Film> films = filmStorage.getCommonFilms(userId, friendId);
        if (films.isEmpty()) {
            return Collections.emptyList();
        }

        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

}
