package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int id = 1;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film get(long id) {
        if (!films.containsKey(id)) {
            log.warn("Ошибка про запросе - фильм с ID {} не найден", id);
            throw new IncorrectIdException("Не найден фильм с ID " + id);
        }

        return films.get(id);
    }

    @Override
    public Film create(Film film) {
        film.setId(id++);
        films.put(film.getId(), film);
        log.debug("Добавлен фильм ID {}", film.getId());
        return film;
    }

    @Override
    public long delete(long id) {
        if (!films.containsKey(id)) {
            log.warn("Ошибка про удалении - фильм с ID {} не найден", id);
            throw new IncorrectIdException("Не найден фильм с ID " + id);
        }

        films.remove(id);
        log.debug("Удален фильм ID {}", id);
        return id;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Ошибка про обновлении - фильм с ID {} не найден", film.getId());
            throw new IncorrectIdException("Не найден фильм с ID " + film.getId());
        }

        films.put(film.getId(), film);
        log.debug("Обновлен фильм ID {}", film.getId());
        return film;
    }
}
