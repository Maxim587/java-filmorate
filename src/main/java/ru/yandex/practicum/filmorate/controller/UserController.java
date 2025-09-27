package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController implements FilmorateController<User> {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public Collection<User> getList() {
        return users.values();
    }

    @Override
    public User create(User user) {
        log.info("Start adding new user");
        user.setId(++id);

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("User with id:{} added", user.getId());
        return user;
    }

    @Override
    public User update(User newUser) {
        log.info("Start updating user with id:{}", newUser.getId());
        if (newUser.getId() == null) {
            log.warn("User updating failed: id not provided");
            throw new ValidationException("Id должен быть указан");
        }

        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            log.warn("User updating failed: user with id:{} not found", newUser.getId());
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

        log.info("User with id:{} updated", oldUser.getId());
        return oldUser;
    }
}
