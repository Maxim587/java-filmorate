package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewReaction;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review createReview(Review review);

    Optional<Review> getReviewById(int reviewId);

    List<Review> getReviewsByFilm(int filmId, int count);

    List<Review> getAllReviews(int count);

    Review updateReview(Review review);

    boolean deleteReview(int reviewId);

    Optional<ReviewReaction> getReviewReaction(int reviewId, int userId);

    void addReviewReaction(int reviewId, int userId, boolean isPositiveReaction, int useful);

    void deleteReviewReaction(int reviewId, int userId, int useful);
}
