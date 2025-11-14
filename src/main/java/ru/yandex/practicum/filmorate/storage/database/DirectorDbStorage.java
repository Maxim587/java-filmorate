package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        String sql = "SELECT * FROM directors ORDER BY director_id";
        return jdbcTemplate.query(sql, directorRowMapper);
    }

    @Override
    public Optional<Director> getDirectorById(int id) {
        String sql = "SELECT * FROM directors WHERE director_id = ?";
        List<Director> directors = jdbcTemplate.query(sql, directorRowMapper, id);
        return directors.isEmpty() ? Optional.empty() : Optional.of(directors.get(0));
    }

    @Override
    public Director createDirector(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id != null) {
            director.setId(id);
        }
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE director_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, director.getName(), director.getId());

        if (rowsUpdated == 0) {
            throw new ru.yandex.practicum.filmorate.exception.NotFoundException(
                    "Режиссёр с id:" + director.getId() + " не найден");
        }

        return director;
    }

    @Override
    public void deleteDirector(int id) {
        // Проверяем существование режиссёра
        Optional<Director> director = getDirectorById(id);
        if (director.isEmpty()) {
            throw new NotFoundException("Режиссёр с id:" + id + " не найден");
        }

        // Сначала получаем ID фильмов, которые связаны с этим режиссером
        String getFilmIdsSql = "SELECT film_id FROM film_director WHERE director_id = ?";
        List<Integer> filmIds = jdbcTemplate.queryForList(getFilmIdsSql, Integer.class, id);

        // Удаляем связи с фильмами (CASCADE уже должно работать, но делаем для надежности)
        String deleteLinksSql = "DELETE FROM film_director WHERE director_id = ?";
        jdbcTemplate.update(deleteLinksSql, id);

        // Затем удаляем самого режиссёра
        String deleteDirectorSql = "DELETE FROM directors WHERE director_id = ?";
        int rowsDeleted = jdbcTemplate.update(deleteDirectorSql, id);

        if (rowsDeleted == 0) {
            throw new NotFoundException("Режиссёр с id:" + id + " не найден");
        }

        // После удаления режиссера, фильмы автоматически обновятся через CASCADE
        // При следующем запросе фильма, он будет загружен из базы без удаленного режиссера
    }
}
