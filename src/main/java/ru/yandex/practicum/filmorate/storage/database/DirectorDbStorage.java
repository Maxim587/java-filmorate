package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Repository
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {

    private static final String FIND_ALL_DIRECTORS_QUERY = "SELECT * FROM DIRECTOR ORDER BY director_id";
    private static final String FIND_DIRECTOR_BY_ID_QUERY = "SELECT * FROM DIRECTOR WHERE director_id = ?";
    private static final String INSERT_DIRECTOR_QUERY = "INSERT INTO DIRECTOR(name) VALUES (?)";
    private static final String UPDATE_DIRECTOR_QUERY = "UPDATE DIRECTOR SET name = ? WHERE director_id = ?";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM DIRECTOR WHERE director_id = ?";

    private final JdbcTemplate jdbc;
    private final RowMapper<Director> directorRowMapper;

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> directorRowMapper) {
        super(jdbc, directorRowMapper);
        this.jdbc = jdbc;
        this.directorRowMapper = directorRowMapper;
    }

    @Override
    public List<Director> getAllDirectors() {
        return findMany(FIND_ALL_DIRECTORS_QUERY);
    }

    @Override
    public Director getDirectorById(int directorId) {
        try {
            return jdbc.queryForObject(FIND_DIRECTOR_BY_ID_QUERY, directorRowMapper, directorId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    @Override
    public Director createDirector(Director director) {
        int id = insert(INSERT_DIRECTOR_QUERY, director.getName());
        director.setId(id);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        update(UPDATE_DIRECTOR_QUERY, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(int directorId) {
        delete(DELETE_DIRECTOR_QUERY, directorId);
    }
}
