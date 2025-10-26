package ru.yandex.practicum.filmorate.storage.inmemory;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InMemoryGenreData {
    private static Map<Integer, Genre> genres;

    private InMemoryGenreData() {
        genres = new HashMap<>();
        genres.put(1, new Genre(1, "Боевик"));
        genres.put(2, new Genre(2, "Комедия"));
        genres.put(3, new Genre(3, "Триллер"));
        genres.put(4, new Genre(4, "Детектив"));
        genres.put(5, new Genre(5, "Ужасы"));
    }

    public static Genre getGenre(int id) {
        Genre genre = genres.get(id);
        if (genre == null) {
            throw new NotFoundException("Жанр не найден: " + id);
        }
        return genres.get(id);
    }

    public static List<Genre> getAllGenres() {
        return new ArrayList<>(genres.values());
    }
}
