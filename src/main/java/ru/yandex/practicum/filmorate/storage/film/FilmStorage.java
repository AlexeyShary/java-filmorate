package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getAll();

    Film get(long id);

    Film create(Film film);

    void delete(long id);

    Film update(Film film);

    List<Long> getUsersLikedFilmsIds(long userId);
}
