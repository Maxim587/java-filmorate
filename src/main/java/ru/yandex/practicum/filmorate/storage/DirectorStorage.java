package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import java.util.List;

public interface DirectorStorage {
    List<Director> getAllDirectors();

    Director getDirectorById(int directorId);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int directorId);
}
