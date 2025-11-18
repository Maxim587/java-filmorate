package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {

    private static final String FIND_ALL_QUERY = """
            SELECT *
            FROM DIRECTOR
            ORDER BY DIRECTOR_ID
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT *
            FROM DIRECTOR
            WHERE DIRECTOR_ID = ?
            """;
    private static final String INSERT_QUERY = """
            INSERT INTO
            DIRECTOR (NAME)
            VALUES (?)
            """;
    private static final String UPDATE_QUERY = """
            UPDATE DIRECTOR
            SET NAME = ?
            WHERE DIRECTOR_ID = ?
            """;
    private static final String DELETE_QUERY = """
            DELETE
            FROM DIRECTOR
            WHERE DIRECTOR_ID = ?
            """;

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Director> getAllDirectors() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Director> getDirectorById(int id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Director createDirector(Director director) {
        int id = insert(INSERT_QUERY, director.getName());
        director.setId(id);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        update(UPDATE_QUERY, director.getName(), director.getId());
        return director;
    }

    @Override
    public boolean deleteDirector(int id) {
        return delete(DELETE_QUERY, id);
    }
}
