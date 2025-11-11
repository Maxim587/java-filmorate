package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserDto {
    @NotBlank(message = "Значение не должно быть пустым")
    @Email(message = "Значение должно соответствовать формату email")
    private String email;

    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
    @NotNull(message = "Значение не должно быть пустым")
    private String login;

    private String name;

    @Past(message = "Дата должна быть в прошлом")
    private LocalDate birthday;
}
