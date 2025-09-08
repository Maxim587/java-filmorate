package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ReleaseDateFrom;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {

    @Positive
    @Max(Integer.MAX_VALUE)
    private Integer id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @ReleaseDateFrom
    private LocalDate releaseDate;

    @Positive
    private int duration;
}
