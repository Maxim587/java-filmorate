package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.DirectorRequestDto;
import ru.yandex.practicum.filmorate.dto.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.MpaRequestDto;
import ru.yandex.practicum.filmorate.validation.ReleaseDateFrom;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateFilmDto {

    @NotNull
    @Max(Integer.MAX_VALUE)
    @Positive(message = "Значение должно быть целым положительным числом")
    private Integer id;

    @NotBlank(message = "Значение не должно быть пустым")
    private String name;

    @Size(max = 200, message = "Длина не должна превышать 200 символов")
    private String description;

    @ReleaseDateFrom
    private LocalDate releaseDate;

    @Positive(message = "Значение должно быть целым положительным числом")
    private int duration;

    @NotNull
    private MpaRequestDto mpa;

    private Set<GenreRequestDto> genres;

    private Set<DirectorRequestDto> directors;
}
