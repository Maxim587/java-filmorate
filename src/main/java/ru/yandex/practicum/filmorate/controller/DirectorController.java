package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public List<DirectorDto> findAll() {
        log.info("Start getting all directors");
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public DirectorDto findById(@PathVariable int id) {
        log.info("Start getting director by id {}", id);
        return directorService.getDirectorById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto create(@RequestBody DirectorDto directorDto) {
        log.info("Start creating director");
        return directorService.createDirector(directorDto);
    }

    @PutMapping
    public DirectorDto update(@RequestBody DirectorDto directorDto) {
        log.info("Start updating director with id: {}", directorDto.getId());
        return directorService.updateDirector(directorDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("Start deleting director with id: {}", id);
        directorService.deleteDirector(id);
    }

    @GetMapping("/{directorId}/films")
    public List<FilmDto> getDirectorFilms(
            @PathVariable int directorId,
            @RequestParam(defaultValue = "year") String sortBy) {
        log.info("Start getting films for director id: {}, sorted by: {}", directorId, sortBy);
        return directorService.getDirectorFilms(directorId, sortBy);
    }
}
