package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final Set<Integer> friends = new HashSet<>();
    @Positive(message = "Значение должно быть целым положительным числом")
    @Max(Integer.MAX_VALUE)
    private Integer id;
    @NotBlank(message = "Значение не должно быть пустым")
    @Email(message = "Значение должно соответствовать формату email")
    private String email;
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
    @NotNull(message = "Значение не должно быть пустым")
    private String login;
    private String name;
    @Past(message = "Дата должна быть в прошлом")
    private LocalDate birthday;

    public boolean addFriend(int friendId) {
        return friends.add(friendId);
    }

    public boolean deleteFriend(int friendId) {
        return friends.remove(friendId);
    }

    public Collection<Integer> getFriendsIds() {
        return friends.stream().toList();
    }
}
