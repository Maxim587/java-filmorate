package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private int id = 0;

    public User create(User user) {
        user.setId(++id);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public Collection<User> findAll() {
        return userStorage.getUsers();
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.info("User updating failed: id not provided");
            throw new ValidationException("Id должен быть указан");
        }

        User oldUser = userStorage.getUserById(newUser.getId());
        if (oldUser == null) {
            log.info("User updating failed: user with id = {} not found", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        if (newUser.getName() == null || newUser.getName().isEmpty()) {
            oldUser.setName(newUser.getLogin());
        } else {
            oldUser.setName(newUser.getName());
        }

        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setBirthday(newUser.getBirthday());

        return oldUser;
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

        user.addFriend(friend.getId());
        friend.addFriend(user.getId());
    }

    public void deleteFriend(int userId, int friendId) {
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

        user.deleteFriend(friend.getId());
        friend.deleteFriend(user.getId());
    }

    public Collection<User> getFriends(int userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Ошибка получения списка друзей. Пользователь с id = " + userId + " не найден");
        }
        return user.getFriendsIds().stream().map(userStorage::getUserById).toList();
    }

    public Collection<User> getCommonFriends(int userId1, int userId2) {
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

        return user1.getFriendsIds().stream()
                .filter(user2.getFriendsIds()::contains)
                .map(userStorage::getUserById)
                .toList();
    }

    public Optional<User> findById(int id) {
        return Optional.ofNullable(userStorage.getUserById(id));
    }
}
