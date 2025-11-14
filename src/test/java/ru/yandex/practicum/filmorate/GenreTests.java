package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreTests {
    private final FilmDbStorage filmDbStorage;

    @Test
    public void testGetGenreById() {
        Optional<Genre> genreOpt = Optional.of(filmDbStorage.getGenreById(1));

        assertThat(genreOpt)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1)
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
