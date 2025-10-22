package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.validation.ReleaseDateFrom;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {

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

    private String rating;

    private List<String> genre;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final Set<Integer> likes = new HashSet<>();


    public boolean addLike(Integer userId) {
        return likes.add(userId);
    }

    public boolean deleteLike(Integer userId) {
        return likes.remove(userId);
    }

    public int getLikesCount() {
        return likes.size();
    }
}
