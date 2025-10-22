package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.ReleaseDateFrom;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

class FilmTests {

    Film newFilm;
    Validator validator;

    @BeforeEach
    public void prepare() {
        newFilm = new Film(null, "nameN", "descriptionNO", LocalDate.of(2002, 5, 15), 120, "R", Stream.of("Horror").toList());
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void addFilmHappyPath() {
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldBeErrorWhenNameIsEmpty() {
        newFilm.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        Assertions.assertInstanceOf(NotBlank.class, violations.stream().findFirst().orElseThrow().getConstraintDescriptor().getAnnotation());
    }

    @Test
    public void shouldBeErrorWhenDescriptionLengthOver200() {
        newFilm.setDescription("s".repeat(201));
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        Assertions.assertInstanceOf(Size.class, violations.stream().findFirst().orElseThrow().getConstraintDescriptor().getAnnotation());
    }

    @Test
    public void shouldBeOkWhenDescriptionLengthNotOver200() {
        newFilm.setDescription("s".repeat(200));
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldBeErrorWhenReleaseDateLess_28_12_1895() {
        newFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        Assertions.assertInstanceOf(ReleaseDateFrom.class, violations.stream().findFirst().orElseThrow().getConstraintDescriptor().getAnnotation());
    }

    @Test
    public void shouldBeOkWhenReleaseDateAfter_28_12_1895() {
        newFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldBeErrorWhenDurationIsNegative() {
        newFilm.setDuration(-220);
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        Assertions.assertInstanceOf(Positive.class, violations.stream().findFirst().orElseThrow().getConstraintDescriptor().getAnnotation());
    }

    @Test
    public void shouldBeErrorWhenDurationIs0() {
        newFilm.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        Assertions.assertInstanceOf(Positive.class, violations.stream().findFirst().orElseThrow().getConstraintDescriptor().getAnnotation());
    }

    @Test
    public void shouldBeOkWhenDurationMoreThan0() {
        newFilm.setDuration(1);
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldBeErrorWhenIdIsNegative() {
        newFilm.setId(-1);
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        Assertions.assertInstanceOf(Positive.class, violations.stream().findFirst().orElseThrow().getConstraintDescriptor().getAnnotation());
    }
}
