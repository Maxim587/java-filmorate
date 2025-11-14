package ru.yandex.practicum.filmorate.storage.database.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.user.FeedDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedRowMapper implements RowMapper<FeedDto> {

    @Override
    public FeedDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        FeedDto feedDto = new FeedDto();
        feedDto.setEventId(resultSet.getInt("event_id"));
        feedDto.setTimestamp(resultSet.getTimestamp("timestamp").toInstant().toEpochMilli());
        feedDto.setUserId(resultSet.getInt("user_id"));
        feedDto.setEntityId(resultSet.getInt("entity_id"));
        feedDto.setEventType(resultSet.getString("event_type"));
        feedDto.setOperation(resultSet.getString("operation"));

        return feedDto;
    }
}
