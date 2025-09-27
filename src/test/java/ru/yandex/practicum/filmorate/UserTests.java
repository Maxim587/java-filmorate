package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

class UserTests {

    User user;
    Validator validator;

    @BeforeEach
    public void prepare() {
        user = new User(null, "example@ex.ru", "login", "name", LocalDate.of(2002, 5, 15));
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void addUserHappyPath() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldBeErrorWhenEmailIsEmpty() {
        user.setEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertInstanceOf(NotBlank.class, violations.stream().findFirst().orElseThrow().getConstraintDescriptor().getAnnotation());
    }

    @Test
    public void shouldBeErrorWhenEmailIsIncorrect() {
        user.setEmail("rr ee.ru");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertInstanceOf(Email.class, violations.stream().findFirst().orElseThrow().getConstraintDescriptor().getAnnotation());
    }

    @Test
    public void shouldBeErrorWhenLoginContainsSpaces() {
        user.setLogin("lo gin");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertInstanceOf(Pattern.class, violations.stream().findFirst().orElseThrow().getConstraintDescriptor().getAnnotation());
    }

    @Test
    public void shouldBeErrorWhenLoginIsNull() {
        user.setLogin(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertInstanceOf(NotNull.class, violations.stream().findFirst().orElseThrow().getConstraintDescriptor().getAnnotation());
    }

    @Test
    public void shouldBeErrorWhenBirthdayInTheFuture() {
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertInstanceOf(Past.class, violations.stream().findFirst().orElseThrow().getConstraintDescriptor().getAnnotation());
    }

    @Test
    public void shouldBeErrorWhenIdIsNegative() {
        user.setId(-1);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertInstanceOf(Positive.class, violations.stream().findFirst().orElseThrow().getConstraintDescriptor().getAnnotation());
    }
}
