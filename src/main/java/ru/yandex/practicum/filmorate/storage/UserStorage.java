package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.user.FeedDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    List<User> getAllUsers();

    User getUserById(int userId);

    User updateUser(User user);

    void addFriend(int userId, int friendId, int friendshipStatusId);

    void updateFriendshipStatus(int userId, int friendId, int friendshipStatusId);

    boolean deleteFriend(int userId, int friendId);

    boolean deleteUserById(int userId);

    List<User> getUserFriends(int userId);

    List<User> findUsersByIds(List<Integer> userIds);

    List<FeedDto> getUserFeed(int userId);
}
