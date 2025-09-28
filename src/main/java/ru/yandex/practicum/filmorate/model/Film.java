package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.validation.ReleaseDateFrom;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final Set<User> likes = new HashSet<>();
    @Positive(message = "Значение должно быть целым положительным числом")
    @Max(Integer.MAX_VALUE)
    private Integer id;
    @NotBlank(message = "Значение не должно быть пустым")
    private String name;
    @Size(max = 200, message = "Длина не должна превышать 200 символов")
    private String description;
    @ReleaseDateFrom
    private LocalDate releaseDate;
    @Positive(message = "Значение должно быть целым положительным числом")
    private int duration;

    public boolean addLike(User user) {
        return likes.add(user);
    }

    public boolean deleteLike(User user) {
        return likes.remove(user);
    }

    public int getLikesCount() {
        return likes.size();
    }
}
