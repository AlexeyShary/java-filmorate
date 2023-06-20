package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Collection<Director> getAll();

    Optional<Director> get(long id);

    Director create(Director director);

    void delete(long id);

    Director update(Director director);
}
