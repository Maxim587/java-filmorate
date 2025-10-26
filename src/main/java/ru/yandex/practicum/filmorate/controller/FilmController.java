package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<FilmDto> findAll() {
        log.info("Start getting all films");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public FilmDto findById(@PathVariable int id) {
        log.info("Start getting film by id {}", id);
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        log.info("Start getting popular films");
        return filmService.getMostPopular(count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody NewFilmDto newFilmDto) {
        log.info("Start adding a new film");
        FilmDto createdFilm = filmService.createFilm(newFilmDto);
        log.info("Film with id:{} added", createdFilm.getId());
        return createdFilm;
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmDto newFilm) {
        log.info("Start updating film with id:{}", newFilm.getId());
        FilmDto updatedFilm = filmService.updateFilm(newFilm);
        log.info("Film with id:{} updated", updatedFilm.getId());
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(id, userId);
    }
}
