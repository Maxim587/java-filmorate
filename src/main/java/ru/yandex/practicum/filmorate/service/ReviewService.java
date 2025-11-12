package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.review.NewReviewDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewReaction;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;

    public ReviewDto createReview(NewReviewDto dto) {
        Review review = ReviewMapper.mapToReview(dto);
        return ReviewMapper.mapToReviewDto(reviewStorage.createReview(review));
    }

    public ReviewDto getReviewById(int id) {
        return reviewStorage.getReviewById(id)
                .map(ReviewMapper::mapToReviewDto)
                .orElseThrow(() -> {
                    log.info("Error while getting review by id. Review not found id: {}", id);
                    return new NotFoundException("Отзыв с id:" + id + " не найден");
                });
    }

    public List<ReviewDto> getReviewsByFilm(Optional<Integer> filmId, int count) {
        List<Review> reviews;
        if (filmId.isPresent()) {
            reviews = reviewStorage.getReviewsByFilm(filmId.get(), count);
        } else {
            reviews = reviewStorage.getAllReviews(count);
        }

        if (reviews.isEmpty()) {
            return Collections.emptyList();
        }

        return reviews.stream()
                .map(ReviewMapper::mapToReviewDto)
                .sorted(Comparator.comparing(ReviewDto::getUseful).reversed())
                .toList();
    }

    public ReviewDto updateReview(UpdateReviewDto updateReviewDto) {
        if (updateReviewDto.getReviewId() == null) {
            log.info("Review updating failed: id not provided");
            throw new ValidationException("Id должен быть указан");
        }

        Review oldReview = reviewStorage.getReviewById(updateReviewDto.getReviewId()).orElseThrow(() -> {
            log.info("Review updating failed: review with id = {} not found", updateReviewDto.getReviewId());
            return new NotFoundException("Отзыв с id = " + updateReviewDto.getReviewId() + " не найден");
        });


        Review updatedReview = ReviewMapper.updateReviewFields(oldReview, updateReviewDto);
        return ReviewMapper.mapToReviewDto(reviewStorage.updateReview(updatedReview));
    }

    public boolean deleteReview(int reviewId) {
        reviewStorage.getReviewById(reviewId).orElseThrow(() -> {
            log.info("Review deleting failed: review with id = {} not found", reviewId);
            return new NotFoundException("Отзыв с id = " + reviewId + " не найден");
        });

        return reviewStorage.deleteReview(reviewId);
    }

    //добавление лайка/дизлайка к отзыву
    public ReviewDto addReactionToReview(int reviewId, int userId, boolean isPositiveReaction) {
        Review review = reviewStorage.getReviewById(reviewId).orElseThrow(() -> {
            log.info("Error while managing review reaction: review with id = {} not found", reviewId);
            return new NotFoundException("Отзыв с id = " + reviewId + " не найден");
        });

        Optional.ofNullable(userStorage.getUserById(userId)).orElseThrow(() -> {
            log.info("Error while adding like: user with id = {} not found", userId);
            return new NotFoundException("Ошибка изменения реакции к отзыву. Пользователь не найден");
        });

        //Производится поиск действующей реакции на отзыв(лайк/дизлайк). Если не найдена, добавляется новая.
        //Если реакции отличаются, происходит замена
        Optional<ReviewReaction> reactionOpt = reviewStorage.getReviewReaction(reviewId, userId);

        if (reactionOpt.isEmpty()) {
            if (isPositiveReaction) {
                review.increaseUseful(false);
            } else {
                review.decreaseUseful(false);
            }
        } else {
            if (reactionOpt.get().isPositive() == isPositiveReaction) {
                throw new ConditionsNotMetException("Отзыв уже имеет реакцию от пользователя");
            }

            if (isPositiveReaction) {
                review.increaseUseful(true);
            } else {
                review.decreaseUseful(true);
            }
        }
        reviewStorage.addReviewReaction(reviewId, userId, isPositiveReaction, review.getUseful());
        return ReviewMapper.mapToReviewDto(review);
    }

    // удаление лайка/дизлайка к отзыву
    public ReviewDto deleteReactionFromReview(int reviewId, int userId, boolean isPositiveReaction) {
        Review review = reviewStorage.getReviewById(reviewId).orElseThrow(() -> {
            log.info("Error while managing review reaction: review with id = {} not found", reviewId);
            return new NotFoundException("Отзыв с id = " + reviewId + " не найден");
        });

        Optional.ofNullable(userStorage.getUserById(userId)).orElseThrow(() -> {
            log.info("Error while managing review reaction: user with id = {} not found", userId);
            return new NotFoundException("Ошибка изменения реакции к отзыву. Пользователь не найден");
        });

        ReviewReaction currentReaction = reviewStorage.getReviewReaction(reviewId, userId).orElseThrow(() -> {
            log.info("Error while managing review reaction: review {} of user {} not found", reviewId, userId);
            return new NotFoundException("Ошибка изменения реакции к отзыву. Реакция не найдена");
        });


        if (currentReaction.isPositive() != isPositiveReaction) {
            throw new ConditionsNotMetException("Отзыв не имеет такой реакции от пользователя");
        }

        if (isPositiveReaction) {
            review.decreaseUseful(false);
        } else {
            review.increaseUseful(false);
        }

        reviewStorage.deleteReviewReaction(reviewId, userId, review.getUseful());
        return ReviewMapper.mapToReviewDto(review);
    }

}
