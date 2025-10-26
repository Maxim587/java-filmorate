package ru.yandex.practicum.filmorate.model;

public enum FriendshipStatus {
    CONFIRMED(1),
    NOT_CONFIRMED(2);
    private final int id;

    FriendshipStatus(int id) {
        this.id = id;
    }

    public static FriendshipStatus fromValue(int id) {
        for (FriendshipStatus status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Некорректное значение id");
    }

    public int getId() {
        return id;
    }
}
