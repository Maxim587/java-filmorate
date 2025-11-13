package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.database.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.database.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.database.mapper.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, ReviewDbStorage.class, ReviewRowMapper.class,
        FilmDbStorage.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class})
public class ReviewsTests {
    private final UserDbStorage userDbStorage;
    private final ReviewDbStorage reviewDbStorage;
    private final FilmDbStorage filmDbStorage;

    User user0;
    Film film0;
    Review review0;

    @BeforeEach
    void setUp() {
        user0 = getUser();
        film0 = getFilm();
        userDbStorage.createUser(user0);
        filmDbStorage.createFilm(film0);
        review0 = new Review(null, "Test review", true, user0.getId(), film0.getId(), 0);
    }

    @Test
    public void createReview() {
        Review review = reviewDbStorage.createReview(review0);

        Optional<Review> dbReview = reviewDbStorage.getReviewById(review.getReviewId());
        assertThat(dbReview.isPresent()).isTrue();
        assertThat(dbReview.get().getContent()).isEqualTo(review.getContent());
        assertThat(dbReview.get().isPositive()).isEqualTo(review.isPositive());
        assertThat(dbReview.get().getUserId()).isEqualTo(review.getUserId());
        assertThat(dbReview.get().getFilmId()).isEqualTo(review.getFilmId());
        assertEquals(0, dbReview.get().getUseful());
    }

    @Test
    public void reviewList() {
        User user1 = userDbStorage.createUser(getUser());
        Film film1 = filmDbStorage.createFilm(getFilm());

        Review review1 = new Review(null, "review1", false, user1.getId(), film0.getId(), 10);
        Review review2 = new Review(null, "review2", true, user1.getId(), film1.getId(), 50);

        reviewDbStorage.createReview(review0);
        reviewDbStorage.createReview(review1);
        reviewDbStorage.createReview(review2);

        List<Review> reviews = reviewDbStorage.getAllReviews(10);

        //должны быть добавлены все отзывы
        assertThat(reviews).contains(review0, review1, review2);

        //отзывы отсортированы по useful по убыванию
        assertThat(reviews.getFirst()).isEqualTo(review2);
        assertThat(reviews.getLast()).isEqualTo(review0);

        //получение отзывов по фильму
        List<Review> reviewsByFilm = reviewDbStorage.getReviewsByFilm(film0.getId(), 10);
        assertThat(reviewsByFilm).hasSize(2)
                .contains(review0, review1);
    }

    @Test
    public void getReviewById() {
        reviewDbStorage.createReview(review0);

        Optional<Review> reviewById = reviewDbStorage.getReviewById(review0.getReviewId());
        assertThat(reviewById.isPresent()).isTrue();
        assertThat(reviewById.get()).isEqualTo(review0);
    }

    @Test
    public void updateReview() {
        Review oldReview = reviewDbStorage.createReview(review0);
        Review newReview = new Review(oldReview.getReviewId(), "new content", false,
                oldReview.getUserId(), oldReview.getFilmId(), oldReview.getUseful());

        reviewDbStorage.updateReview(newReview);
        Optional<Review> dbReview = reviewDbStorage.getReviewById(oldReview.getReviewId());
        assertThat(dbReview.isPresent()).isTrue();
        assertThat(dbReview.get().getContent()).isEqualTo(newReview.getContent());
        assertThat(dbReview.get().isPositive()).isEqualTo(newReview.isPositive());
        assertThat(dbReview.get().getUserId()).isEqualTo(newReview.getUserId());
        assertThat(dbReview.get().getFilmId()).isEqualTo(newReview.getFilmId());
        assertThat(dbReview.get().getUseful()).isEqualTo(newReview.getUseful());
    }

    @Test
    public void deleteReview() {
        Review review = reviewDbStorage.createReview(review0);
        boolean deleted = reviewDbStorage.deleteReview(review.getReviewId());

        assertThat(deleted).isTrue();
        assertThat(reviewDbStorage.getReviewById(review.getReviewId()).isEmpty()).isTrue();
    }

    @Test
    public void reviewReactions() {
        User user1 = userDbStorage.createUser(getUser());
        reviewDbStorage.createReview(review0);

        //добавляем реакцию
        reviewDbStorage.addReviewReaction(review0.getReviewId(), user1.getId(), true, 1);
        //получаем реакцию пользователя на отзыв
        Optional<ReviewReaction> reaction = reviewDbStorage.getReviewReaction(review0.getReviewId(), user1.getId());
        assertThat(reaction.isPresent()).isTrue(); //проверяем что есть
        assertThat(reaction.get().getUserId()).isEqualTo(user1.getId()); //проверяем что пользователь совпадает
        assertThat(reaction.get().isPositive()).isTrue(); //проверяем что реакция положительная
        Optional<Review> reviewDb = reviewDbStorage.getReviewById(review0.getReviewId()); //получаем отзыв с реакцией
        assertEquals(1, reviewDb.get().getUseful()); //проверяем, что полезность отзыва увеличена


        //удаляем реакцию
        reviewDbStorage.deleteReviewReaction(review0.getReviewId(), user1.getId(),  0);
        //получаем реакцию пользователя на отзыв
        reaction = reviewDbStorage.getReviewReaction(review0.getReviewId(), user1.getId());
        assertThat(reaction.isPresent()).isFalse(); //проверяем что реакции нет
        reviewDb = reviewDbStorage.getReviewById(review0.getReviewId()); //получаем отзыв с реакцией
        assertEquals(0, reviewDb.get().getUseful()); //проверяем, что полезность отзыва уменьшена

    }

    private User getUser() {
        User user = new User();
        user.setEmail("ex@ex.ru");
        user.setLogin("test");
        user.setName("name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    private Film getFilm() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2025, 11, 11));
        film.setMpa(new Mpa(1, "G"));
        film.setGenres(Set.of(new Genre(1, "Комедия")));
        return film;
    }
}
