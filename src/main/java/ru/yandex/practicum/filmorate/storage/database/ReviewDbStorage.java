package ru.yandex.practicum.filmorate.storage.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewReaction;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.database.mapper.ReviewReactionRowMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@Primary
@Repository
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {

    private static final String INSERT_QUERY = "INSERT " +
            "INTO REVIEW(CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL)" +
            "VALUES(?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_QUERY = "SELECT " +
            "REVIEW_ID, CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL " +
            "FROM REVIEW " +
            "WHERE REVIEW_ID = ?";
    private static final String FIND_REVIEWS_BY_FILM_QUERY = "SELECT " +
            "REVIEW_ID, CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL " +
            "FROM REVIEW " +
            "WHERE FILM_ID = ? " +
            "ORDER BY USEFUL DESC " +
            "LIMIT ?";
    private static final String FIND_ALL_REVIEWS_QUERY = "SELECT " +
            "REVIEW_ID, CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL " +
            "FROM REVIEW " +
            "ORDER BY USEFUL DESC " +
            "LIMIT ?";
    private static final String UPDATE_QUERY = "UPDATE REVIEW " +
            "SET CONTENT = ?, IS_POSITIVE = ? " +
            "WHERE REVIEW_ID = ?";
    private static final String DELETE_REVIEW_QUERY =
            "DELETE FROM REVIEW WHERE REVIEW_ID = ?";
    private static final String FIND_REVIEW_REACTION_QUERY = "SELECT " +
            "REVIEW_ID, USER_ID, IS_POSITIVE " +
            "FROM REVIEW_LIKE " +
            "WHERE REVIEW_ID = ? " +
            "AND USER_ID = ?";
    private static final String UPDATE_REVIEW_USEFUL_QUERY = "UPDATE REVIEW " +
            "SET USEFUL = ? " +
            "WHERE REVIEW_ID = ?";
    private static final String ADD_REVIEW_REACTION_QUERY = "MERGE " +
            "INTO REVIEW_LIKE (review_id, user_id, is_positive) KEY (review_id, user_id) " +
            "VALUES (?, ?, ?)";
    private static final String DELETE_REVIEW_REACTION_QUERY = "DELETE " +
            "FROM REVIEW_LIKE " +
            "WHERE REVIEW_ID = ? AND USER_ID = ?";


    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review createReview(Review review) {
        int id = insert(
                INSERT_QUERY,
                review.getContent(),
                review.isPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful()
        );
        review.setReviewId(id);
        return review;
    }

    @Override
    public Optional<Review> getReviewById(int reviewId) {
        return findOne(FIND_BY_ID_QUERY, reviewId);
    }

    @Override
    public List<Review> getReviewsByFilm(int filmId, int count) {
        return findMany(FIND_REVIEWS_BY_FILM_QUERY, filmId, count);
    }

    @Override
    public List<Review> getAllReviews(int count) {
        return findMany(FIND_ALL_REVIEWS_QUERY, count);
    }

    @Override
    public Review updateReview(Review review) {
        update(
                UPDATE_QUERY,
                review.getContent(),
                review.isPositive(),
                review.getReviewId()
        );
        return review;
    }

    @Override
    public boolean deleteReview(int reviewId) {
        return delete(DELETE_REVIEW_QUERY, reviewId);
    }

    @Override
    public Optional<ReviewReaction> getReviewReaction(int reviewId, int userId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_REVIEW_REACTION_QUERY, new ReviewReactionRowMapper(), reviewId, userId));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void addReviewReaction(int reviewId, int userId, boolean isPositiveReaction, int useful) {
        //добавление лайка/дизлайка к отзыву
        update(ADD_REVIEW_REACTION_QUERY, reviewId, userId, isPositiveReaction);
        update(UPDATE_REVIEW_USEFUL_QUERY, useful, reviewId);
    }

    @Override
    public void deleteReviewReaction(int reviewId, int userId, int useful) {
        // удаление лайка/дизлайка к отзыву
        update(DELETE_REVIEW_REACTION_QUERY, reviewId, userId);
        update(UPDATE_REVIEW_USEFUL_QUERY, useful, reviewId);
    }
}
