package ru.yandex.practicum.filmorate.storage.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.*;

@Slf4j
@Primary
@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private static final String FIND_ALL_QUERY =
            "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY =
            "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users " +
            "SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String FIND_ALL_USERS_FRIENDS_QUERY = "SELECT f.user_id, f.friend_id, fs.status " +
            "FROM friendship f " +
            "JOIN friendship_status fs ON f.friendship_status_id = fs.friendship_status_id " +
            "WHERE f.user_id IN (:ids)";
    private static final String UPDATE_FRIENDSHIP_STATUS_QUERY = "UPDATE friendship " +
            "SET friendship_status_id = ? " +
            "WHERE user_id = ? AND friend_id = ?;";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friendship (user_id, friend_id, friendship_status_id) " +
            "VALUES (?, ?, ?);";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friendship " +
            "WHERE user_id = ? AND friend_id = ?";

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
        List<User> users = findMany(FIND_ALL_QUERY);
        if (users.isEmpty()) {
            return users;
        }

        List<Integer> userIds = users.stream().map(User::getId).toList();
        Map<Integer, List<Friendship>> usersFriends = findFriends(userIds);
        if (usersFriends.isEmpty()) {
            return users;
        }

        usersFriends.forEach((k, v) -> {
            User user = users.stream()
                    .filter(usr -> Objects.equals(usr.getId(), k))
                    .findFirst()
                    .get();
            v.forEach(user::addFriend);
        });
        return users;
    }

    @Override
    public User getUserById(int userId) {
        User user = findOne(FIND_BY_ID_QUERY, userId);
        if (user != null) {
            List<Friendship> friendship = findFriends(List.of(userId)).get(userId);
            if (friendship != null) {
                friendship.forEach(user::addFriend);
            }
        }
        return user;
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

    public Map<Integer, List<Friendship>> findFriends(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            log.warn("В метод не переданы id пользователей");
            throw new IllegalArgumentException("В метод не переданы id пользователей");
        }
        NamedParameterJdbcTemplate namedJdbc = new NamedParameterJdbcTemplate(jdbc);
        Map<String, List<Integer>> paramMap = Collections.singletonMap("ids", userIds);

        return namedJdbc.query(FIND_ALL_USERS_FRIENDS_QUERY, paramMap, (ResultSet rs) -> {
            try {
                Map<Integer, List<Friendship>> results = new HashMap<>();
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    int friendId = rs.getInt("friend_id");
                    FriendshipStatus status = FriendshipStatus.valueOf(rs.getString("status"));
                    results.computeIfAbsent(userId, k -> new ArrayList<>()).add(new Friendship(friendId, status));
                }
                return results;
            } catch (IllegalArgumentException e) {
                log.error("Ошибка получения данных из БД. {}", String.valueOf(e));
                throw new InternalServerException("Не удалось получить данные");
            }
        });
    }
}
