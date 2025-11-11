package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
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
@Import({FilmDbStorage.class, FilmRowMapper.class, MpaRowMapper.class, GenreRowMapper.class})
public class MpaTests {
    private final FilmDbStorage filmDbStorage;
    private final FilmRowMapper filmRowMapper;
    private final MpaRowMapper mpaRowMapper;
    private final GenreRowMapper genreRowMapper;

    @Test
    public void testGetMpaById() {

        Optional<Mpa> mpaOpt = Optional.of(filmDbStorage.getRatingById(1));

        assertThat(mpaOpt)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testGetAllMpas() {

        List<Mpa> mpas = filmDbStorage.getRatings();

        assertThat(mpas)
                .isNotEmpty()
                .hasSize(5);

        assertThat(mpas.get(0))
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G");
    }
}
