package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewReviewDto {
    @NotNull(message = "Значение не должно быть пустым")
    @NotBlank(message = "Значение не должно быть пустым")
    private String content;

    @NotNull(message = "Значение не должно быть пустым")
    private Boolean isPositive;

    @NotNull(message = "Значение не должно быть пустым")
    private Integer userId;

    @NotNull(message = "Значение не должно быть пустым")
    private Integer filmId;
}
