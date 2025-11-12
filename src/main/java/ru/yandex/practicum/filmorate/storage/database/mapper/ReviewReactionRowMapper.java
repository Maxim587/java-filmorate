package ru.yandex.practicum.filmorate.storage.database.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewReaction;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewReactionRowMapper implements RowMapper<ReviewReaction> {

    @Override
    public ReviewReaction mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ReviewReaction reaction = new ReviewReaction();
        reaction.setReviewId(resultSet.getInt("review_id"));
        reaction.setUserId(resultSet.getInt("user_id"));
        reaction.setPositive(resultSet.getBoolean("is_positive"));

        return reaction;
    }
}
