package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserIntegrationTests {
    private final UserDbStorage userDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void getAllUsers() {
        User user = userDbStorage.createUser(getUser());
        User user2 = userDbStorage.createUser(getUser());

        List<User> users = userDbStorage.getAllUsers();
        assertThat(users).hasSize(2);

        User checkUser = users.getFirst();
        assertThat(checkUser.getId()).isEqualTo(user.getId());
        assertThat(checkUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(checkUser.getLogin()).isEqualTo(user.getLogin());
        assertThat(checkUser.getName()).isEqualTo(user.getName());
        assertThat(checkUser.getBirthday()).isEqualTo(user.getBirthday());
    }

    @Test
    public void updateUser() {
        User oldUser = userDbStorage.createUser(getUser());
        User newUser = new User();
        newUser.setId(oldUser.getId());
        newUser.setEmail("m@m.ru");
        newUser.setLogin("newLogin");
        newUser.setName("newName");
        newUser.setBirthday(oldUser.getBirthday().plusDays(111));

        userDbStorage.updateUser(newUser);

        User updatedUserFromDb = userDbStorage.getUserById(oldUser.getId());
        assertThat(updatedUserFromDb.getId()).isEqualTo(oldUser.getId());
        assertThat(updatedUserFromDb.getEmail()).isEqualTo(newUser.getEmail());
        assertThat(updatedUserFromDb.getLogin()).isEqualTo(newUser.getLogin());
        assertThat(updatedUserFromDb.getName()).isEqualTo(newUser.getName());
        assertThat(updatedUserFromDb.getBirthday()).isEqualTo(newUser.getBirthday());
    }

    @Test
    public void friendshipChecks() {
        User user1 = userDbStorage.createUser(getUser());
        User user2 = userDbStorage.createUser(getUser());

        // добавление в друзья
        userDbStorage.addFriend(user1.getId(), user2.getId(), 2);
        Map<Integer, Friendship> user1Friends = userDbStorage.getUserById(user1.getId()).getFriends();
        assertThat(user1Friends).hasSize(1);
        Friendship user1AndUser2Friendship = user1Friends.get(user2.getId());
        assertThat(user1AndUser2Friendship).isNotNull(); // сущность Дружба != null
        assertThat(user1AndUser2Friendship.getFriendId()).isEqualTo(user2.getId()); // в сущности Дружба нужный userId
        assertThat(user1AndUser2Friendship.getStatus()).isEqualTo(FriendshipStatus.NOT_CONFIRMED.toString()); // в сущности Дружба нужный статус
        assertThat(userDbStorage.getUserById(user2.getId()).getFriends()).isEmpty(); // у второго пользователя не должен появится в друзьях user1

        // изменение статуса дружбы
        userDbStorage.addFriend(user1.getId(), user2.getId(), 1);
        assertThat(userDbStorage.getUserById(user1.getId()).getFriends().get(user2.getId()).getStatus()).isEqualTo(FriendshipStatus.CONFIRMED.toString());

        // удаление
        userDbStorage.deleteFriend(user1.getId(), user2.getId());
        assertThat(userDbStorage.getUserById(user1.getId()).getFriends()).isEmpty();
    }

    @Test
    public void deleteUserById() {
        User user = userDbStorage.createUser(getUser());
        int userId = user.getId();

        assertThat(userDbStorage.getUserById(userId)).isNotNull();

        boolean deleted = userDbStorage.deleteUserById(userId);

        assertThat(deleted).isTrue();
        assertThat(userDbStorage.getUserById(userId)).isNull();
    }

    private User getUser() {
        User user = new User();
        user.setEmail("ex@ex.ru");
        user.setLogin("test");
        user.setName("name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }
}
