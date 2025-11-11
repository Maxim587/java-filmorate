package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorDto;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public DirectorDto createDirector(NewDirectorDto newDirectorDto) {
        Director director = DirectorMapper.mapToDirector(newDirectorDto);
        return DirectorMapper.mapToDirectorDto(directorStorage.createDirector(director));
    }

    public List<DirectorDto> getAllDirectors() {
        List<Director> directors = directorStorage.getAllDirectors();
        if (directors.isEmpty()) {
            return Collections.emptyList();
        }
        return directors.stream()
                .map(DirectorMapper::mapToDirectorDto)
                .toList();
    }

    public DirectorDto getDirectorById(int id) {
        return Optional.ofNullable(directorStorage.getDirectorById(id))
                .map(DirectorMapper::mapToDirectorDto)
                .orElseThrow(() -> {
                    log.info("Error while getting director by id. Director not found id: {}", id);
                    return new NotFoundException("Режиссер с id:" + id + " не найден");
                });
    }

    public DirectorDto updateDirector(UpdateDirectorDto updateDirectorDto) {
        if (updateDirectorDto.getId() == null) {
            log.info("Director updating failed: id not provided");
            throw new ValidationException("Id должен быть указан");
        }

        Director directorToUpdate = directorStorage.getDirectorById(updateDirectorDto.getId());
        if (directorToUpdate == null) {
            log.info("Director updating failed: director with id:{} not found", updateDirectorDto.getId());
            throw new NotFoundException("Режиссер с id = " + updateDirectorDto.getId() + " не найден");
        }

        DirectorMapper.updateDirectorFields(directorToUpdate, updateDirectorDto);
        return DirectorMapper.mapToDirectorDto(directorStorage.updateDirector(directorToUpdate));
    }

    public void deleteDirector(int id) {
        Director director = directorStorage.getDirectorById(id);
        if (director == null) {
            log.info("Error while deleting director. Director not found id: {}", id);
            throw new NotFoundException("Режиссер с id:" + id + " не найден");
        }
        directorStorage.deleteDirector(id);
    }
}
