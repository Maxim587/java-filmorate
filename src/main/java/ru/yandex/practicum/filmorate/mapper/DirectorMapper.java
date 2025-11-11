package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorDto;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectorMapper {
    public static DirectorDto mapToDirectorDto(Director director) {
        DirectorDto directorDto = new DirectorDto();
        directorDto.setId(director.getId());
        directorDto.setName(director.getName());
        return directorDto;
    }

    public static Director mapToDirector(NewDirectorDto newDirectorDto) {
        Director director = new Director();
        director.setName(newDirectorDto.getName());
        return director;
    }

    public static Director updateDirectorFields(Director oldDirector, UpdateDirectorDto updateDirectorDto) {
        oldDirector.setName(updateDirectorDto.getName());
        return oldDirector;
    }
}
