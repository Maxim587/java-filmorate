package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.review.NewReviewDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewMapper {
    public static ReviewDto mapToReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReviewId(review.getId());
        reviewDto.setContent(review.getContent());
        reviewDto.setIsPositive(review.isPositive());
        reviewDto.setUserId(review.getUserId());
        reviewDto.setFilmId(review.getFilmId());
        reviewDto.setUseful(review.getUseful());

        return reviewDto;
    }

    public static Review mapToReview(NewReviewDto newReviewDto) {
        Review review = new Review();
        review.setContent(newReviewDto.getContent());
        review.setPositive(newReviewDto.getIsPositive());
        if (newReviewDto.getFilmId() <= 0) {
            throw new NotFoundException("FilmId должен быть положительным числом");
        }
        review.setFilmId(newReviewDto.getFilmId());
        if (newReviewDto.getUserId() <= 0) {
            throw new NotFoundException("UserId должен быть положительным числом");
        }
        review.setUserId(newReviewDto.getUserId());

        return review;
    }

    public static Review updateReviewFields(Review oldReview, UpdateReviewDto updateReviewDto) {
        oldReview.setContent(updateReviewDto.getContent());
        oldReview.setPositive(updateReviewDto.getIsPositive());

        return oldReview;
    }

}
