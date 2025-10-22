package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    Collection<User> getUsers();

    User getUserById(int userId);

    User updateUser(User user);

    void updateFriendshipStatus(int userId, int friendId, int friendshipStatusId);

    void addFriend(int userId, int friendId, int friendshipStatusId);

    boolean deleteFriend(int userId, int friendId);
}
