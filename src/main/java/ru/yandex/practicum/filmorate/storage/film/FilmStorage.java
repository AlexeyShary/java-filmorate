package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAll();

    Film get(long id);

    Film create(Film film);

    long delete(long id);

    Film update(Film film);
}
