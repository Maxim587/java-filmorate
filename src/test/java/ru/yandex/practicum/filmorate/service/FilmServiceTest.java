package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.factory.FilmFactory;
import ru.yandex.practicum.filmorate.factory.UserFactory;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.database.UserDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {
    private final UserFactory userFactory = new UserFactory();
    private final FilmFactory filmFactory = new FilmFactory();

    private FilmDbStorage filmStorage;
    private UserDbStorage userStorage;
    private FilmService filmService;

    private List<User> users;
    private List<Film> films;

    @BeforeEach
    void setUp() {
        filmStorage = mock(FilmDbStorage.class);
        userStorage = mock(UserDbStorage.class);
        filmService = new FilmService(
                filmStorage,
                userStorage,
                mock(DirectorStorage.class)
        );

        users = userFactory.makeModelsList(2);
        films = filmFactory.makeModelsList(3);

        // likes setup
        like(1, 1);
        like(2, 1);
        like(2, 2);
        like(3, 2);

        when(
                userStorage.getAllUsers()
        ).thenReturn(
                users
        );
        when(
                userStorage.getUserById(anyInt())
        ).thenAnswer(
                inv -> getUser(inv.getArgument(0))
        );
        when(
                filmStorage.getUserLikedFilms(anyInt())
        ).thenAnswer(
                inv -> getLikedFilms(inv.getArgument(0))
        );
        lenient().when(
                filmStorage.getRecommended(1, 2)
        ).thenReturn(
                getLikedFilms(2) // Liked by User 2
                        .stream()
                        .filter(film -> !film.getLikes().contains(1)) // But not liked by User 1
                        .toList()
        );
    }

    @Test
    void shouldRecommendFilmsFromSimilarUser() {
        List<FilmDto> recommended = filmService.getRecommended(1);

        assertEquals(1, recommended.size(), "Expected one recommendation");
        assertEquals(3, recommended.get(0).getId(), "Expected Film 3 to be recommended");
    }

    @Test
    void shouldReturnEmptyListWhenNoSimilarUser() {
        unlike(2, 2);
        unlike(3, 2);

        List<FilmDto> recommended = filmService.getRecommended(1);

        assertTrue(recommended.isEmpty(), "Expected no recommendations");
    }

    private void like(int filmId, int userId) {
        getFilm(filmId).getLikes().add(userId);
    }

    private void unlike(int filmId, int userId) {
        getFilm(filmId).getLikes().remove(userId);
    }

    private Film getFilm(int id) {
        return films.stream()
                .filter(f -> f.getId() == id)
                .findFirst()
                .orElseThrow();
    }

    private User getUser(int id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private List<Film> getLikedFilms(int userId) {
        return films.stream()
                .filter(f -> f.getLikes().contains(userId))
                .toList();
    }
}