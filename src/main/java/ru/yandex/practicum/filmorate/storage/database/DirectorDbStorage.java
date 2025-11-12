package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Director> directorRowMapper = (rs, rowNum) -> {
        Director director = new Director();
        director.setId(rs.getInt("director_id"));
        director.setName(rs.getString("name"));
        return director;
    };

    @Override
    public List<Director> getAllDirectors() {
        String sql = "SELECT * FROM director ORDER BY director_id";
        return jdbcTemplate.query(sql, directorRowMapper);
    }

    @Override
    public Optional<Director> getDirectorById(int id) {
        String sql = "SELECT * FROM director WHERE director_id = ?";
        List<Director> directors = jdbcTemplate.query(sql, directorRowMapper, id);
        return directors.isEmpty() ? Optional.empty() : Optional.of(directors.get(0));
    }

    @Override
    public Director createDirector(Director director) {
        String sql = "INSERT INTO director (name) VALUES (?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        director.setId(keyHolder.getKeyAs(Integer.class));
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE director SET name = ? WHERE director_id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        String sql = "DELETE FROM director WHERE director_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
