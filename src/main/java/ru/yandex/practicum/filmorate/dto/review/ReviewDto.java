package ru.yandex.practicum.filmorate.dto.review;

import lombok.Data;

@Data
public class ReviewDto {
    private Integer reviewId;
    private String content;
    private Boolean isPositive;
    private Integer userId;
    private Integer filmId;
    private int useful;
}
