package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReviewDto {
    @NotNull(message = "Значение не должно быть пустым")
    private Integer reviewId;
    @NotNull(message = "Значение не должно быть пустым")
    @NotBlank(message = "Значение не должно быть пустым")
    private String content;
    @NotNull(message = "Значение не должно быть пустым")
    private Boolean isPositive;
}
