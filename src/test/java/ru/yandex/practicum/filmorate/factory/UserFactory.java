package ru.yandex.practicum.filmorate.factory;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public class UserFactory extends Factory<User> {
    public User makeModel() {
        return new User(
                makeInteger(),
                makeEmail(),
                makeString(),
                makeString(),
                makeDate()
        );
    }

    @Override
    public List<User> makeModelsList(int size) {
        final int[] lastId = {1};
        return super.makeModelsList(size)
                .stream()
                .peek(
                        (user) -> {
                            user.setId(lastId[0]);
                            lastId[0]++;
                        }
                )
                .toList();
    }
}
