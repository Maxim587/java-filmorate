package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.NewUserDto;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserDto newUserDto) {
        log.info("Start adding new user");
        UserDto userDto = userService.createUser(newUserDto);
        log.info("User with id:{} added", userDto.getId());
        return userDto;
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserDto updateUserDto) {
        log.info("Start updating user with id:{}", updateUserDto.getId());
        UserDto userDto = userService.updateUser(updateUserDto);
        log.info("User with id:{} updated", userDto.getId());
        return userDto;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Start adding friend with id = {} to user with id = {}", friendId, id);
        userService.addFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> getUserFriends(@PathVariable int id) {
        log.info("Start getting user's friends for user id = {}", id);
        return userService.getUserFriends(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public boolean deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Start deleting friend with id = {} from user with id = {}", friendId, id);
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Start getting common friends for user id = {} and user id = {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
