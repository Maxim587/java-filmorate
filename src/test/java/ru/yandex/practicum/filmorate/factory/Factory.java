package ru.yandex.practicum.filmorate.factory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Factory<M> {
    public abstract M makeModel();

    public List<M> makeModelsList(int size) {
        List<M> list = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            list.add(makeModel());
        }

        return list;
    }

    protected Integer makeInteger() {
        return Math.toIntExact(Math.round(
                Math.random() * Math.pow(10, 2)
        ));
    }

    protected String makeString() {
        return makeInteger().toString();
    }

    protected LocalDate makeDate() {
        return LocalDate.ofEpochDay(
                makeInteger()
        );
    }

    protected String makeEmail() {
        return String.format(
                "%s@%s.com",
                makeString(),
                makeString()
        );
    }
}
