package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {

    @Positive
    @Max(Integer.MAX_VALUE)
    private Integer id;

    @NotBlank
    @Email
    private String email;

    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
    @NotNull
    private String login;

    private String name;

    @Past
    private LocalDate birthday;
}
