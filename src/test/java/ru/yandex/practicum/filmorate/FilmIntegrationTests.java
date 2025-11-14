package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.database.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.database.mapper.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, UserDbStorage.class, UserRowMapper.class, FilmRowMapper.class,
        GenreRowMapper.class, MpaRowMapper.class, DirectorDbStorage.class, DirectorRowMapper.class})
public class FilmIntegrationTests {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;


    @Test
    public void createFilm() {
        Film film = filmDbStorage.createFilm(prepareFilms().getFirst());

        Film dbFilm = filmDbStorage.getFilmById(film.getId());
        assertThat(dbFilm).isNotNull();
        assertThat(dbFilm.getId()).isEqualTo(film.getId());
        assertThat(dbFilm.getName()).isEqualTo("name");
        assertThat(dbFilm.getDescription()).isEqualTo("description");
        assertThat(dbFilm.getDuration()).isEqualTo(120);
        assertThat(dbFilm.getReleaseDate()).isEqualTo(LocalDate.of(2025, 11, 11));
        assertThat(dbFilm.getMpa().getId()).isEqualTo(1);
    }

    @Test
    public void getAllFilms() {
        List<Film> preparedFilms = prepareFilms();

        Film film1 = filmDbStorage.createFilm(preparedFilms.getFirst());
        Film film2 = filmDbStorage.createFilm(preparedFilms.getLast());

        List<Film> films = filmDbStorage.getAllFilms();
        assertThat(films).hasSize(2);

        Film film = films.getFirst();
        assertThat(film.getId()).isEqualTo(film1.getId());
        assertThat(film.getName()).isEqualTo("name");
        assertThat(film.getDescription()).isEqualTo("description");
        assertThat(film.getDuration()).isEqualTo(120);
        assertThat(film.getReleaseDate()).isEqualTo(LocalDate.of(2025, 11, 11));
        assertThat(film.getMpa().getId()).isEqualTo(1);
    }

    @Test
    public void updateFilm() {
        List<Film> preparedFilms = prepareFilms();
        Film oldFilm = filmDbStorage.createFilm(preparedFilms.getFirst());
        Film newFilm = preparedFilms.getLast();
        newFilm.setId(oldFilm.getId());
        filmDbStorage.updateFilm(newFilm);

        Film updatedFilmFromDb = filmDbStorage.getFilmById(oldFilm.getId());
        assertThat(updatedFilmFromDb.getId()).isEqualTo(oldFilm.getId());
        assertThat(updatedFilmFromDb.getName()).isEqualTo(newFilm.getName());
        assertThat(updatedFilmFromDb.getDescription()).isEqualTo(newFilm.getDescription());
        assertThat(updatedFilmFromDb.getDuration()).isEqualTo(newFilm.getDuration());
        assertThat(updatedFilmFromDb.getReleaseDate()).isEqualTo(newFilm.getReleaseDate());
        assertThat(updatedFilmFromDb.getMpa().getId()).isEqualTo(newFilm.getMpa().getId());
    }

    @Test
    public void filmLikes() {
        User user = prepareUser();
        User dbUser = userDbStorage.createUser(user);
        Film dbFilm = filmDbStorage.createFilm(prepareFilms().getFirst());

        filmDbStorage.addLike(dbFilm.getId(), dbUser.getId());

        List<Integer> likes = filmDbStorage.getFilmLikes(dbFilm.getId());
        assertThat(likes).hasSize(1);
        assertThat(likes).contains(dbUser.getId());

        filmDbStorage.deleteLike(dbFilm.getId(), dbUser.getId());
        List<Integer> likes2 = filmDbStorage.getFilmLikes(dbFilm.getId());
        assertThat(likes2).isEmpty();
    }

    @Test
    public void getMostPopularFilm() {
        Film film1 = filmDbStorage.createFilm(prepareFilms().getFirst());
        Film film2 = filmDbStorage.createFilm(prepareFilms().getLast());
        Film film3 = filmDbStorage.createFilm(prepareFilms().getFirst());
        Film film4 = filmDbStorage.createFilm(prepareFilms().getLast());
        Film film5 = filmDbStorage.createFilm(prepareFilms().getFirst());
        Film film6 = filmDbStorage.createFilm(prepareFilms().getLast());

        User user1 = userDbStorage.createUser(prepareUser());
        User user2 = userDbStorage.createUser(prepareUser());
        User user3 = userDbStorage.createUser(prepareUser());
        User user4 = userDbStorage.createUser(prepareUser());
        User user5 = userDbStorage.createUser(prepareUser());
        User user6 = userDbStorage.createUser(prepareUser());
        User user7 = userDbStorage.createUser(prepareUser());

        filmDbStorage.addLike(film6.getId(), user1.getId());
        filmDbStorage.addLike(film6.getId(), user2.getId());
        filmDbStorage.addLike(film6.getId(), user3.getId());
        filmDbStorage.addLike(film6.getId(), user4.getId());

        filmDbStorage.addLike(film3.getId(), user5.getId());
        filmDbStorage.addLike(film3.getId(), user6.getId());
        filmDbStorage.addLike(film3.getId(), user7.getId());

        filmDbStorage.addLike(film1.getId(), user5.getId());
        filmDbStorage.addLike(film1.getId(), user6.getId());

        filmDbStorage.addLike(film2.getId(), user3.getId());

        List<Film> top = filmDbStorage.getMostPopular(3);
        assertThat(top).hasSize(3);
        assertThat(top.getFirst().getId()).isEqualTo(film6.getId());
        assertThat(top.get(1).getId()).isEqualTo(film3.getId());
        assertThat(top.getLast().getId()).isEqualTo(film1.getId());
    }

    @Test
    public void deleteFilmById() {
        Film film = filmDbStorage.createFilm(prepareFilms().getFirst());
        int filmId = film.getId();

        assertThat(filmDbStorage.getFilmById(filmId)).isNotNull();

        boolean deleted = filmDbStorage.deleteFilmById(filmId);

        assertThat(deleted).isTrue();
        assertThat(filmDbStorage.getFilmById(filmId)).isNull();
    }

    private List<Film> prepareFilms() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2025, 11, 11));
        film.setMpa(new Mpa(1, "G"));
        film.setGenres(Set.of(new Genre(1, "Комедия")));

        Film film2 = new Film();
        film2.setName("name2");
        film2.setDescription("description2");
        film2.setDuration(100);
        film2.setReleaseDate(LocalDate.of(2025, 1, 11));
        film2.setMpa(new Mpa(2, "PG"));
        film2.setGenres(Set.of(new Genre(2, "Драма")));

        return List.of(film, film2);
    }

    private User prepareUser() {
        User user = new User();
        user.setEmail("ex@ex.ru");
        user.setLogin("test");
        user.setName("name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }
}
