package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ReleaseDateFromValidator implements ConstraintValidator<ReleaseDateFrom, LocalDate> {
    private static final LocalDate RELEASE_DATE_LOWER_BOUNDARY = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.isAfter(RELEASE_DATE_LOWER_BOUNDARY) || value.equals(RELEASE_DATE_LOWER_BOUNDARY);
        }
        return false;
    }
}
