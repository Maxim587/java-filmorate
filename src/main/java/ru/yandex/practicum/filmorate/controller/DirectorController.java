package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorDto;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public List<Director> findAll() {
        log.info("Start getting all directors");
        List<Director> directors = directorService.getAllDirectors();
        log.info("Found {} directors", directors.size());
        return directors;
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable int id) {
        log.info("Start getting director by id {}", id);
        try {
            Director director = directorService.getDirectorById(id);
            log.info("Found director: {}", director);
            return director;
        } catch (NotFoundException e) {
            log.error("Director not found with id: {}", id);
            throw e;
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@Valid @RequestBody NewDirectorDto newDirectorDto) {
        log.info("Start adding a new director");
        Director createdDirector = directorService.createDirector(newDirectorDto);
        log.info("Director with id:{} added", createdDirector.getId());
        return createdDirector;
    }

    @PutMapping
    public Director update(@Valid @RequestBody UpdateDirectorDto updateDirectorDto) {
        log.info("Start updating director with id:{}", updateDirectorDto.getId());
        Director updatedDirector = directorService.updateDirector(updateDirectorDto);
        log.info("Director with id:{} updated", updatedDirector.getId());
        return updatedDirector;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("Start deleting director with id:{}", id);
        directorService.deleteDirector(id);
    }
}
