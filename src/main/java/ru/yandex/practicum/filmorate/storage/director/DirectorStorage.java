package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {
    Collection<Director> getAll();

    Director get(long id);

    Director create(Director director);

    void delete(long id);

    Director update(Director director);
}
