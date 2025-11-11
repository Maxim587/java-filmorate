package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateDirectorDto {
    @NotNull
    @Positive(message = "ID должен быть положительным числом")
    private Integer id;

    @NotBlank(message = "Имя режиссера не может быть пустым")
    private String name;
}
