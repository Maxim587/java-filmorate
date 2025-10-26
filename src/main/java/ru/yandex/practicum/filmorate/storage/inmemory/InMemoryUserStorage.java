package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public User createUser(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return users.values().stream().toList();
    }

    @Override
    public User getUserById(int userId) {
        return users.get(userId);
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void updateFriendshipStatus(int userId, int friendId, int friendshipStatusId) {
        try {
            users.get(userId).getFriends().get(friendId).setFriendshipStatus(FriendshipStatus.fromValue(friendshipStatusId));
        } catch (IllegalArgumentException e) {
            log.error("Ошибка обновления статуса дружбы. Передан некорректный параметр friendshipStatusId: {} \n {}", friendshipStatusId, String.valueOf(e));
            throw new InternalServerException("Ошибка обновления данных");
        }
    }

    @Override
    public void addFriend(int userId, int friendId, int friendshipStatusId) {
        try {
            users.get(userId).addFriend(new Friendship(friendId, FriendshipStatus.fromValue(friendshipStatusId)));
        } catch (IllegalArgumentException e) {
            log.error("Ошибка добавления в друзья. Передан некорректный параметр friendshipStatusId: {} \n {}", friendshipStatusId, String.valueOf(e));
            throw new InternalServerException("Ошибка обновления данных");
        }
    }

    @Override
    public boolean deleteFriend(int userId, int friendId) {
        return false;
    }
}
