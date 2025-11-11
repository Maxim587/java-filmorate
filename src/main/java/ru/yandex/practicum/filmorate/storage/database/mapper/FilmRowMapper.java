package ru.yandex.practicum.filmorate.storage.database.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(new Mpa(rs.getInt("rating_id"), rs.getString("rating_name")));

        int genreId = rs.getInt("genre_id");
        if (genreId != 0) {
            film.getGenres().add(new Genre(rs.getInt("genre_id"), rs.getString("genre")));
        }

        int like = rs.getInt("like");
        if (like != 0) {
            film.getLikes().add(like);
        }

        int directorId = rs.getInt("director_id");
        if (directorId != 0) {
            film.getDirectors().add(new Director(rs.getInt("director_id"), rs.getString("director_name")));
        }

        return film;
    }
}
