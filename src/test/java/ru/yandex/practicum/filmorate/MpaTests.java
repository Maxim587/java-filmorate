package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaTests {
    private final FilmDbStorage filmDbStorage;

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

        assertThat(mpas.getFirst())
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G");
    }
}
