package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Primary
@Repository
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {

    private final JdbcTemplate jdbc;
    private final RowMapper<Director> directorRowMapper;

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> directorRowMapper) {
        super(jdbc, directorRowMapper);
        this.jdbc = jdbc;
        this.directorRowMapper = directorRowMapper;
    }

    private static final String FIND_ALL_QUERY = "SELECT * FROM DIRECTORS";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM DIRECTORS WHERE director_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO DIRECTORS(name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE DIRECTORS SET name = ? WHERE director_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM DIRECTORS WHERE director_id = ?";

    @Override
    public Director createDirector(Director director) {
        int id = insert(INSERT_QUERY, director.getName());
        director.setId(id);
        return director;
    }

    @Override
    public List<Director> getAllDirectors() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Director getDirectorById(int directorId) {
        try {
            return jdbc.queryForObject(FIND_BY_ID_QUERY, directorRowMapper, directorId);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    @Override
    public Director updateDirector(Director director) {
        update(UPDATE_QUERY, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(int directorId) {
        delete(DELETE_QUERY, directorId);
    }
}
