package ru.yandex.practicum.filmorate.storage.database.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));

        // Безопасное получение даты
        java.sql.Date releaseDate = rs.getDate("release_date");
        if (releaseDate != null) {
            film.setReleaseDate(releaseDate.toLocalDate());
        } else {
            film.setReleaseDate(LocalDate.now()); // или другое значение по умолчанию
        }

        film.setDuration(rs.getInt("duration"));

        // Безопасное создание MPA
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("rating_id"));
        String ratingName = rs.getString("rating_name");
        mpa.setName(ratingName != null ? ratingName : "Unknown");
        film.setMpa(mpa);

        // Безопасное добавление жанров
        int genreId = rs.getInt("genre_id");
        if (!rs.wasNull() && genreId != 0) {
            String genreName = rs.getString("genre");
            if (genreName != null) {
                film.getGenres().add(new Genre(genreId, genreName));
            }
        }

        // Безопасное добавление лайков
        int like = rs.getInt("like");
        if (!rs.wasNull() && like != 0) {
            film.getLikes().add(like);
        }

        // Гарантируем, что коллекции не null
        if (film.getGenres() == null) film.setGenres(new HashSet<>());
        if (film.getLikes() == null) film.setLikes(new HashSet<>());
        if (film.getDirectors() == null) film.setDirectors(new HashSet<>());

        return film;
    }
}
