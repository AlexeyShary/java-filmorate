package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class FilmService {
    private int id = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    public Collection<Film> getAllFilms() {
        return films.values();
    }

    public Film createFilm(Film film) {
        film.setId(id++);
        films.put(film.getId(), film);
        log.debug("Добавлен фильм ID {}", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Попытка обновить несуществующий фильм с ID {}", film.getId());
            throw new ValidationException();
        }

        films.put(film.getId(), film);
        log.debug("Обновлен фильм ID {}", film.getId());
        return film;
    }
}
