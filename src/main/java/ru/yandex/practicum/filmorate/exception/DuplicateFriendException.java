package ru.yandex.practicum.filmorate.exception;

public class DuplicateFriendException extends RuntimeException {
    public DuplicateFriendException(String message) {
        super(message);
    }
}
