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
                .orElseThrow(() -> new NotFoundException("Режиссер с id:" + id + " не найден"));
    }

    public Director createDirector(NewDirectorDto newDirectorDto) {
        Director director = new Director();
        director.setName(newDirectorDto.getName());
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(UpdateDirectorDto updateDirectorDto) {
        Director existingDirector = getDirectorById(updateDirectorDto.getId());
        existingDirector.setName(updateDirectorDto.getName());
        return directorStorage.updateDirector(existingDirector);
    }

    public void deleteDirector(int id) {
        getDirectorById(id); // Проверка существования
        directorStorage.deleteDirector(id);
    }
}
