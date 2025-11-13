package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorDto;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(int id) {
        return directorStorage.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Режиссёр с id:" + id + " не найден"));
    }

    public Director createDirector(NewDirectorDto newDirectorDto) {
        Director director = new Director();
        director.setName(newDirectorDto.getName());
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(UpdateDirectorDto updateDirectorDto) {
        // Проверяем существование режиссёра перед обновлением
        directorStorage.getDirectorById(updateDirectorDto.getId())
                .orElseThrow(() -> new NotFoundException("Режиссёр с id:" + updateDirectorDto.getId() + " не найден"));

        Director director = new Director();
        director.setId(updateDirectorDto.getId());
        director.setName(updateDirectorDto.getName());
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(int id) {
        // Проверяем существование режиссёра перед удалением
        directorStorage.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Режиссёр с id:" + id + " не найден"));

        directorStorage.deleteDirector(id);
    }
}
