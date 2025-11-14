package ru.yandex.practicum.filmorate.storage.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Primary
@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private static final String FIND_ALL_QUERY =
            "SELECT u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY, f.FRIEND_ID, fs.STATUS " +
                    "FROM USERS u " +
                    "LEFT JOIN FRIENDSHIP f USING(USER_ID) " +
                    "LEFT JOIN FRIENDSHIP_STATUS fs ON f.FRIENDSHIP_STATUS_ID = fs.FRIENDSHIP_STATUS_ID";
    private static final String FIND_BY_ID_QUERY =
            "SELECT u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY, f.FRIEND_ID, fs.STATUS " +
                    "FROM USERS u " +
                    "LEFT JOIN FRIENDSHIP f USING(USER_ID) " +
                    "LEFT JOIN FRIENDSHIP_STATUS fs ON f.FRIENDSHIP_STATUS_ID = fs.FRIENDSHIP_STATUS_ID " +
                    "WHERE u.USER_ID = ?";
    private static final String FIND_USERS_BY_IDS_QUERY =
            "SELECT u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY, f.FRIEND_ID, fs.STATUS " +
                    "FROM USERS u " +
                    "LEFT JOIN FRIENDSHIP f USING(USER_ID) " +
                    "LEFT JOIN FRIENDSHIP_STATUS fs ON f.FRIENDSHIP_STATUS_ID = fs.FRIENDSHIP_STATUS_ID " +
                    "WHERE u.USER_ID IN (:param)";
    private static final String INSERT_QUERY = "INSERT INTO USERS(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE USERS " +
            "SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String UPDATE_FRIENDSHIP_STATUS_QUERY = "UPDATE FRIENDSHIP " +
            "SET friendship_status_id = ? " +
            "WHERE user_id = ? AND friend_id = ?;";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO FRIENDSHIP (user_id, friend_id, friendship_status_id) " +
            "VALUES (?, ?, ?);";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM FRIENDSHIP " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_USER_FRIENDS_QUERY = "SELECT u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY, f2.FRIEND_ID, fs.STATUS " +
            "FROM FRIENDSHIP f1 " +
            "JOIN USERS u ON f1.FRIEND_ID = u.USER_ID " +
            "LEFT JOIN FRIENDSHIP f2 ON u.USER_ID = f2.USER_ID left JOIN FRIENDSHIP_STATUS fs ON f2.FRIENDSHIP_STATUS_ID = fs.FRIENDSHIP_STATUS_ID " +
            "WHERE f1.USER_ID = ? " +
            "ORDER BY u.USER_ID";
    private static final String DELETE_USER_QUERY = "DELETE FROM USERS WHERE user_id = ?";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User createUser(User user) {
        int id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday())
        );
        user.setId(id);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> rawUsers = findMany(FIND_ALL_QUERY);
        if (rawUsers.isEmpty()) {
            return rawUsers;
        }
        return groupValues(rawUsers);
    }

    @Override
    public User getUserById(int userId) {
        List<User> rawUsers = findMany(FIND_BY_ID_QUERY, userId);
        if (rawUsers.isEmpty()) {
            return null;
        }
        return groupValues(rawUsers).getFirst();
    }

    @Override
    public User updateUser(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );
        return user;
    }

    @Override
    public void addFriend(int userId, int friendId, int friendshipStatusId) {
        update(ADD_FRIEND_QUERY, userId, friendId, friendshipStatusId);
    }

    @Override
    public void updateFriendshipStatus(int userId, int friendId, int friendshipStatusId) {
        update(UPDATE_FRIENDSHIP_STATUS_QUERY, friendshipStatusId, userId, friendId);
    }

    @Override
    public boolean deleteFriend(int userId, int friendId) {
        return delete(DELETE_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public List<User> getUserFriends(int userId) {
        List<User> rawFriends = findMany(FIND_USER_FRIENDS_QUERY, userId);
        if (rawFriends.isEmpty()) {
            return rawFriends;
        }
        return groupValues(rawFriends);
    }

    private List<User> groupValues(List<User> rawUsers) {
        Map<Integer, User> users = new HashMap<>();
        for (User user : rawUsers) {
            users.compute(user.getId(), (id, usr) -> {
                if (usr == null) {
                    return user;
                }
                for (Friendship friendship : user.getFriends().values()) {
                    usr.addFriend(friendship);
                }
                return usr;
            });
        }
        return users.values().stream().toList();
    }

    @Override
    public List<User> findUsersByIds(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            log.warn("В метод не переданы id пользователей");
            throw new IllegalArgumentException("В метод не переданы id пользователей");
        }

        return findManyByParamList(FIND_USERS_BY_IDS_QUERY, userIds, mapper);
    }

    @Override
    public boolean deleteUserById(int userId) {
        int rowsAffected = jdbc.update(DELETE_USER_QUERY, userId);
        return rowsAffected > 0;
    }

}
