package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.database.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.database.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.database.mapper.MpaRowMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class})
public class GenreTests {
    private final FilmDbStorage filmDbStorage;

    @Test
    public void testGetGenreById() {

        Optional<Genre> genreOpt = Optional.of(filmDbStorage.getGenreById(1));

        assertThat(genreOpt)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrProperty("name")
                );
    }

    @Test
    public void testGetAllGenres() {

        List<Genre> genres = filmDbStorage.getAllGenres();

        assertThat(genres)
                .isNotEmpty();

        assertThat(genres.getFirst())
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrProperty("name");
    }
}
