package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"reviewId"})
public class Review {
    @Positive(message = "Значение должно быть целым положительным числом")
    private Integer reviewId;
    @NotBlank(message = "Значение не должно быть пустым")
    private String content;
    private boolean isPositive;
    @NotNull(message = "Значение не должно быть пустым")
    private Integer userId;
    @NotNull(message = "Значение не должно быть пустым")
    private Integer filmId;
    private int useful = 0;

    public void increaseUseful(boolean isReactionChanged) {
        useful++;
        if (isReactionChanged) {
            useful++;
        }
    }

    public void decreaseUseful(boolean isReactionChanged) {
        useful--;
        if (isReactionChanged) {
            useful--;
        }
    }
}
