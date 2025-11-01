package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.time.LocalDate;
import java.util.Collection;

@Data
public class UserDto {
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Collection<Friendship> friendsIds;
}
