package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;


public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setName(user.getName());
        dto.setBirthday(user.getBirthday());
        dto.setFriendsIds(user.getFriends().values());
        return dto;
    }

    public static User mapToUser(NewUserDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setLogin(dto.getLogin());
        if (dto.getName() == null || dto.getName().isEmpty()) {
            user.setName(dto.getLogin());
        } else {
            user.setName(dto.getName());
        }
        user.setBirthday(dto.getBirthday());

        return user;
    }

    public static User updateUserFields(User oldUser, UpdateUserDto updateUserDto) {
        oldUser.setEmail(updateUserDto.getEmail());
        oldUser.setLogin(updateUserDto.getLogin());
        if (updateUserDto.getName() == null || updateUserDto.getName().isEmpty()) {
            oldUser.setName(updateUserDto.getLogin());
        } else {
            oldUser.setName(updateUserDto.getName());
        }
        oldUser.setBirthday(updateUserDto.getBirthday());
        return oldUser;
    }
}
