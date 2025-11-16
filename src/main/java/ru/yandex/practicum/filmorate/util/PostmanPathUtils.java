package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.exception.ValidationException;

public class PostmanPathUtils {

    private PostmanPathUtils() {
        // utility class
    }

    public static int parsePostmanPathVariable(String variable) {
        if (variable == null) {
            throw new ValidationException("ID не может быть null");
        }

        String cleanVariable = variable;
        if (cleanVariable.startsWith(":")) {
            cleanVariable = cleanVariable.substring(1);
        }

        try {
            return Integer.parseInt(cleanVariable);
        } catch (NumberFormatException e) {
            throw new ValidationException("Некорректный ID: " + variable);
        }
    }
}
