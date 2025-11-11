package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director createDirector(Director director);

    List<Director> getAllDirectors();

    Director getDirectorById(int directorId);

    Director updateDirector(Director director);

    void deleteDirector(int directorId);
}
