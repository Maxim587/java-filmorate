package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class User {

    private final Map<Integer, Friendship> friends = new HashMap<>();
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

    public void addFriend(Friendship friendship) {
        friends.put(friendship.getFriendId(), friendship);
    }

    public Set<Integer> getFriendsIds() {
        return friends.keySet();
    }
}
