package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper; // Добавляем импорт
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage; // Добавляем FilmStorage

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;
    private final FilmStorage filmStorage; // Добавляем FilmStorage для получения фильмов

    public List<DirectorDto> getAllDirectors() {
        return directorStorage.getAllDirectors().stream()
                .map(this::mapToDirectorDto)
                .toList();
    }

    public DirectorDto getDirectorById(int id) {
        return Optional.ofNullable(directorStorage.getDirectorById(id))
                .map(this::mapToDirectorDto)
                .orElseThrow(() -> new NotFoundException("Режиссер с id:" + id + " не найден"));
    }

    public DirectorDto createDirector(DirectorDto directorDto) {
        Director director = mapToDirector(directorDto);
        Director createdDirector = directorStorage.createDirector(director);
        return mapToDirectorDto(createdDirector);
    }

    public DirectorDto updateDirector(DirectorDto directorDto) {
        if (directorStorage.getDirectorById(directorDto.getId()) == null) {
            throw new NotFoundException("Режиссер с id:" + directorDto.getId() + " не найден");
        }
        Director director = mapToDirector(directorDto);
        Director updatedDirector = directorStorage.updateDirector(director);
        return mapToDirectorDto(updatedDirector);
    }

    public void deleteDirector(int id) {
        if (directorStorage.getDirectorById(id) == null) {
            throw new NotFoundException("Режиссер с id:" + id + " не найден");
        }
        directorStorage.deleteDirector(id);
    }

    public List<FilmDto> getDirectorFilms(int directorId, String sortBy) {
        if (directorStorage.getDirectorById(directorId) == null) {
            throw new NotFoundException("Режиссер с id:" + directorId + " не найден");
        }

        // Используем filmStorage для получения фильмов режиссера
        List<Film> films = filmStorage.getDirectorFilms(directorId, sortBy);

        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    private DirectorDto mapToDirectorDto(Director director) {
        DirectorDto dto = new DirectorDto();
        dto.setId(director.getId());
        dto.setName(director.getName());
        return dto;
    }

    private Director mapToDirector(DirectorDto directorDto) {
        Director director = new Director();
        director.setId(directorDto.getId());
        director.setName(directorDto.getName());
        return director;
    }
}
