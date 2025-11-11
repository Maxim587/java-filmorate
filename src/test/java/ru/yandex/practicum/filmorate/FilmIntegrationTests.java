package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.database.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.database.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.database.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.database.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.database.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, UserDbStorage.class, UserRowMapper.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class})
public class FilmIntegrationTests {

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private UserDbStorage userDbStorage;

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
        // Создаем фильмы
        Film film1 = filmDbStorage.createFilm(prepareFilms().getFirst());
        Film film2 = filmDbStorage.createFilm(prepareFilms().getLast());
        Film film3 = filmDbStorage.createFilm(prepareFilms().getFirst());
        Film film4 = filmDbStorage.createFilm(prepareFilms().getLast());
        Film film5 = filmDbStorage.createFilm(prepareFilms().getFirst());
        Film film6 = filmDbStorage.createFilm(prepareFilms().getLast());

        // Создаем пользователей
        User user1 = userDbStorage.createUser(prepareUser());
        User user2 = userDbStorage.createUser(prepareUser());
        User user3 = userDbStorage.createUser(prepareUser());
        User user4 = userDbStorage.createUser(prepareUser());
        User user5 = userDbStorage.createUser(prepareUser());
        User user6 = userDbStorage.createUser(prepareUser());
        User user7 = userDbStorage.createUser(prepareUser());

        // Распределяем лайки так, чтобы film6 был самым популярным (4 лайка)
        filmDbStorage.addLike(film6.getId(), user1.getId());
        filmDbStorage.addLike(film6.getId(), user2.getId());
        filmDbStorage.addLike(film6.getId(), user3.getId());
        filmDbStorage.addLike(film6.getId(), user4.getId());

        // film3 - второй по популярности (3 лайка)
        filmDbStorage.addLike(film3.getId(), user5.getId());
        filmDbStorage.addLike(film3.getId(), user6.getId());
        filmDbStorage.addLike(film3.getId(), user7.getId());

        // film1 - третий по популярности (2 лайка)
        filmDbStorage.addLike(film1.getId(), user5.getId());
        filmDbStorage.addLike(film1.getId(), user6.getId());

        // film2 - четвертый по популярности (1 лайк)
        filmDbStorage.addLike(film2.getId(), user3.getId());

        // Получаем топ-3 популярных фильма
        List<Film> top = filmDbStorage.getMostPopular(3);
        assertThat(top).hasSize(3);

        // Проверяем, что фильмы отсортированы по убыванию количества лайков
        // Собираем ID фильмов в топе для отладки
        List<Integer> topFilmIds = top.stream().map(Film::getId).toList();

        // Проверяем, что film6 (4 лайка) на первом месте
        assertThat(top.get(0).getId()).isEqualTo(film6.getId());
        // Проверяем, что film3 (3 лайка) на втором месте
        assertThat(top.get(1).getId()).isEqualTo(film3.getId());
        // Проверяем, что film1 (2 лайка) на третьем месте
        assertThat(top.get(2).getId()).isEqualTo(film1.getId());

        // Дополнительная проверка: film2 (1 лайк) не должен быть в топ-3
        assertThat(topFilmIds).doesNotContain(film2.getId());
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
