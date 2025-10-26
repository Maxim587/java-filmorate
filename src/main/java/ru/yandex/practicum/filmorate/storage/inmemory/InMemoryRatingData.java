package ru.yandex.practicum.filmorate.storage.inmemory;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InMemoryRatingData {
    private static Map<Integer, Mpa> ratings;

    private InMemoryRatingData() {
        ratings = new HashMap<>();
        ratings.put(1, new Mpa(1, "G"));
        ratings.put(2, new Mpa(2, "PG"));
        ratings.put(3, new Mpa(3, "PG-13"));
        ratings.put(4, new Mpa(4, "R"));
        ratings.put(5, new Mpa(5, "NC-17"));
    }

    public static Mpa getRating(int id) {
        Mpa rating = ratings.get(id);
        if (rating == null) {
            throw new NotFoundException("Неправильно указан рейтинг: " + id);
        }
        return ratings.get(id);
    }

    public static List<Mpa> getAllRatings() {
        return new ArrayList<>(ratings.values());
    }
}
