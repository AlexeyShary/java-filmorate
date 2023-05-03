package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film get(long id) {
        return filmStorage.get(id);
    }

    public Collection<Film> getPopular(long count) {
        return getAll().stream()
                .sorted(Comparator.comparingInt(i -> -i.getLikedUsersIds().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public long delete(long id) {
        return filmStorage.delete(id);
    }

    public void addLike(long id, long userId) {
        Film film = get(id);
        User user = userStorage.get(userId);

        film.getLikedUsersIds().add(user.getId());

        log.debug("Добавлен лайк фильму {} от пользователя {}", id, userId);
    }

    public void deleteLike(long id, long userId) {
        Film film = get(id);
        User user = userStorage.get(userId);

        if (!film.getLikedUsersIds().contains(userId)) {
            log.warn("Ошибка про удалении лайка с фильма - пользователь с ID {} не лайкал фильм {}", userId, id);
            throw new IncorrectIdException("Пользователь с ID " + userId + " не лайкал фильм " + id);
        }

        film.getLikedUsersIds().remove(user.getId());

        log.debug("Удален лайк фильму {} от пользователя {}", id, userId);
    }
}