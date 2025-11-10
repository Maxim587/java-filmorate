package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserDto;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicateFriendException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserDto createUser(NewUserDto dto) {
        User user = UserMapper.mapToUser(dto);
        return UserMapper.mapToUserDto(userStorage.createUser(user));
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userStorage.getAllUsers();
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto getUserById(int id) {
        return Optional.ofNullable(userStorage.getUserById(id))
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> {
                    log.info("Error while getting user by id. User not found id: {}", id);
                    return new NotFoundException("Пользователь с id:" + id + " не найден");
                });
    }

    public UserDto updateUser(UpdateUserDto updateUserDto) {
        if (updateUserDto.getId() == null) {
            log.info("User updating failed: id not provided");
            throw new ValidationException("Id должен быть указан");
        }
        User oldUser = userStorage.getUserById(updateUserDto.getId());
        if (oldUser == null) {
            log.info("User updating failed: user with id = {} not found", updateUserDto.getId());
            throw new NotFoundException("Пользователь с id = " + updateUserDto.getId() + " не найден");
        }
        User updatedUser = UserMapper.updateUserFields(oldUser, updateUserDto);
        return UserMapper.mapToUserDto(userStorage.updateUser(updatedUser));
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.info("User with id = {} not found", userId);
            throw new NotFoundException("Ошибка добавления пользователя в друзья. " +
                    "Пользователь с id = " + userId + " не найден");
        }
        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            log.info("User with id = {} not found", friendId);
            throw new NotFoundException("Ошибка добавления пользователя в друзья. " +
                    "Пользователь с id = " + friendId + " не найден");
        }
        if (user.getFriends().containsKey(friendId)) {
            log.info("User with id = {} has already been added to friends", friendId);
            throw new DuplicateFriendException("Ошибка добавления пользователя в друзья. " +
                    "Пользователь с id = " + friendId + " уже добавлен");
        }

        boolean isReciprocalFriendship = friend.getFriends().containsKey(userId);
        int friendshipStatusId = FriendshipStatus.NOT_CONFIRMED.getId();
        if (isReciprocalFriendship) {
            friendshipStatusId = FriendshipStatus.CONFIRMED.getId();
            userStorage.updateFriendshipStatus(friendId, userId, friendshipStatusId);
        }
        userStorage.addFriend(userId, friendId, friendshipStatusId);
    }

    public Collection<UserDto> getUserFriends(int userId) {
        Optional.ofNullable(userStorage.getUserById(userId)).orElseThrow(() ->
                new NotFoundException("Ошибка получения списка друзей. Пользователь с id = " + userId + " не найден"));

        return userStorage.getUserFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public boolean deleteFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.info("User with id = {} not found", userId);
            throw new NotFoundException("Ошибка удаления пользователя из друзей. " +
                    "Пользователь с id = " + userId + " не найден");
        }

        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            log.info("User with id = {} not found", friendId);
            throw new NotFoundException("Ошибка удаления пользователя из друзей. " +
                    "Пользователь с id = " + friendId + " не найден");
        }

        boolean isReciprocalFriendship = friend.getFriends().containsKey(userId);
        if (isReciprocalFriendship) {
            userStorage.updateFriendshipStatus(friendId, userId, FriendshipStatus.NOT_CONFIRMED.getId());
        }
        return userStorage.deleteFriend(userId, friendId);
    }

    public Collection<UserDto> getCommonFriends(int userId1, int userId2) {
        User user1 = userStorage.getUserById(userId1);
        if (user1 == null) {
            log.info("User with id = {} not found", userId1);
            throw new NotFoundException("Ошибка получения общих друзей. Пользователь id:" + userId1 + " не найден");
        }

        User user2 = userStorage.getUserById(userId2);
        if (user2 == null) {
            log.info("User with id = {} not found", userId2);
            throw new NotFoundException("Ошибка получения общих друзей. Пользователь id:" + userId2 + " не найден");
        }

        List<Integer> commonFriendsIds = user1.getFriendsIds().stream()
                .filter(user2.getFriendsIds()::contains)
                .toList();

        if (commonFriendsIds.isEmpty()) {
            return Collections.emptyList();
        }

        return userStorage.findUsersByIds(commonFriendsIds).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public void deleteUserById(int userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        userStorage.deleteUserById(userId);
    }

}
