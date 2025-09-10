package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

public interface FilmorateController<T> {

    @GetMapping
    Collection<T> getList();

    @PostMapping
    T create(@Valid @RequestBody T entity);

    @PutMapping
    T update(@Valid @RequestBody T entity);
}
