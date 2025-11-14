package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class ReviewReaction {
    private Integer reviewId;
    private Integer userId;
    private boolean isPositive;
}
