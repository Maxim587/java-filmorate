package ru.yandex.practicum.filmorate.factory;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;
import java.util.List;

public class FilmFactory extends Factory<Film> {
    public Film makeModel() {
        return new Film(
                makeInteger(),
                makeString(),
                makeString(),
                makeDate(),
                makeInteger(),
                null,
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>()
        );
    }

    @Override
    public List<Film> makeModelsList(int size) {
        final int[] lastId = {1};
        return super.makeModelsList(size)
                .stream()
                .peek(
                        (film) -> {
                            film.setId(lastId[0]);
                            lastId[0]++;
                        }
                )
                .toList();
    }
}
