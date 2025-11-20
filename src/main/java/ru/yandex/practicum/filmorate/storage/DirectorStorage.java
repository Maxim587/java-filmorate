package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    List<Director> getAllDirectors();

    Optional<Director> getDirectorById(int id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    boolean deleteDirector(int id);
}
