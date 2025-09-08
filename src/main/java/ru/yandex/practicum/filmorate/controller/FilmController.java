package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends AbstractController<Film> {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getList() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        log.info("Start adding a new film");
        film.setId(getNextId(films));
        films.put(film.getId(), film);
        log.info("Film with id:{} added", film.getId());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        log.info("Start updating film with id:{}", newFilm.getId());
        if (newFilm.getId() == null) {
            log.warn("Film updating failed: id not provided");
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());

            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());

            log.info("Film with id:{} updated", oldFilm.getId());
            return oldFilm;
        }
        log.warn("Film updating failed: film with id:{} not found", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }
}
