package ru.yandex.practicum.filmorate.dto.director;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
public class DirectorRequestDto {
    private int id;
}
