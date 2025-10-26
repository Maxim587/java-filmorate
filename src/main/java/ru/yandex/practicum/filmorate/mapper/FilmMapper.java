package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {
    public static FilmDto mapToFilmDto(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());
        filmDto.setMpa(film.getMpa());
        filmDto.setGenres(film.getGenres());
        filmDto.setLikes(film.getLikes());

        return filmDto;
    }

    public static Film mapToFilm(NewFilmDto newFilmDto, Mpa mpa, List<Genre> genres) {
        Film film = new Film();
        film.setName(newFilmDto.getName());
        film.setDescription(newFilmDto.getDescription());
        film.setReleaseDate(newFilmDto.getReleaseDate());
        film.setDuration(newFilmDto.getDuration());
        film.setMpa(mpa);
        film.setGenres(genres);

        return film;
    }

    public static Film updateFilmFields(Film oldFilm, UpdateFilmDto updateFilmDto) {
        oldFilm.setName(updateFilmDto.getName());
        oldFilm.setDescription(updateFilmDto.getDescription());
        oldFilm.setReleaseDate(updateFilmDto.getReleaseDate());
        oldFilm.setDuration(updateFilmDto.getDuration());

        return oldFilm;
    }

}
