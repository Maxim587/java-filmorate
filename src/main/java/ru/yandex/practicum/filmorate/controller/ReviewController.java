package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.NewReviewDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewDto;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto create(@Valid @RequestBody NewReviewDto newReviewDto) {
        log.info("Start adding new review");
        ReviewDto reviewDto = reviewService.createReview(newReviewDto);
        log.info("Review with id:{} added", reviewDto.getReviewId());
        return reviewDto;
    }

    @GetMapping("/{id}")
    public ReviewDto findReviewById(@PathVariable int id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<ReviewDto> findReviewsByFilm(
            @RequestParam Optional<Integer> filmId,
            @RequestParam(defaultValue = "10") int count
    ) {
        log.info("Start getting reviews by film");

        return reviewService.getReviewsByFilm(filmId, count);
    }

    @PutMapping
    public ReviewDto update(@Valid @RequestBody UpdateReviewDto updateReviewDto) {
        log.info("Start updating review with id:{}", updateReviewDto.getReviewId());
        ReviewDto reviewDto = reviewService.updateReview(updateReviewDto);
        log.info("Review with id:{} updated", reviewDto.getReviewId());
        return reviewDto;
    }

    @DeleteMapping("/{id}")
    public boolean deleteReview(@PathVariable int id) {
        log.info("Start deleting review with id = {}", id);
        return reviewService.deleteReview(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public ReviewDto addLikeToReview(@PathVariable int id, @PathVariable int userId) {
        log.info("Start adding like to review with id:{}", id);
        ReviewDto reviewDto = reviewService.addReactionToReview(id, userId, true);
        log.info("Like to review with id:{} put", id);
        return reviewDto;
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ReviewDto addDislikeToReview(@PathVariable int id, @PathVariable int userId) {
        log.info("Start adding dislike to review with id:{}", id);
        ReviewDto reviewDto = reviewService.addReactionToReview(id, userId, false);
        log.info("Dislike to review with id:{} put", id);
        return reviewDto;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ReviewDto deleteLikeFromReview(@PathVariable int id, @PathVariable int userId) {
        log.info("Start deleting like from review with id = {}", id);
        return reviewService.deleteReactionFromReview(id, userId, true);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ReviewDto deleteDislikeFromReview(@PathVariable int id, @PathVariable int userId) {
        log.info("Start deleting dislike from review with id = {}", id);
        return reviewService.deleteReactionFromReview(id, userId, false);
    }
}
