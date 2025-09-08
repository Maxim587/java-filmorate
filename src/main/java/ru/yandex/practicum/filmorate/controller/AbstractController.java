package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.Map;

abstract class AbstractController<T> {

    @GetMapping
    public abstract Collection<T> getList();

    @PostMapping
    public abstract T create(@Valid @RequestBody T entity);

    @PutMapping
    public abstract T update(@Valid @RequestBody T entity);

    protected int getNextId(Map<Integer, T> entities) {
        int currentMaxId = entities.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
