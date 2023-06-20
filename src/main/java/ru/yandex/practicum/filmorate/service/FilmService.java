package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Qualifier("likesDbStorage")
    private final LikesStorage likesStorage;

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film get(long id) {
        return filmStorage.get(id);
    }

    public Collection<Film> getCommon(long userId, long friendId) {
        Collection<Long> commonFilmsIds = likesStorage.getCommonFilmsIds(userId, friendId);
        return filmStorage.getFilmsByListIds(commonFilmsIds);

    }

    public Collection<Film> getPopular(long count) {
        /*
        return getAll().stream()
                .sorted(Comparator.comparingInt(i -> -i.getLikedUsersIds().size()))
                .limit(count)
                .collect(Collectors.toList());*/

        return likesStorage.getPopularFilmsIds(count).stream()
                .map(filmStorage::get)
                .collect(Collectors.toList());
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void delete(long id) {
        filmStorage.delete(id);
    }

    public void addLike(long id, long userId) {
        Film film = get(id);
        User user = userStorage.get(userId);

        film.getLikedUsersIds().add(user.getId());

        likesStorage.addLike(userId, id);

        log.debug("Добавлен лайк фильму {} от пользователя {}", id, userId);
    }

    public void deleteLike(long id, long userId) {
        Film film = get(id);
        User user = userStorage.get(userId);

        if (!film.getLikedUsersIds().contains(userId)) {
            log.warn("Ошибка про удалении лайка с фильма - пользователь с ID {} не лайкал фильм {}", userId, id);
            throw new IncorrectIdException("Пользователь с ID " + userId + " не лайкал фильм " + id);
        }

        likesStorage.deleteLike(userId, id);

        log.debug("Удален лайк фильму {} от пользователя {}", id, userId);
    }

    public Collection<Film> getDirectorFilmsSorted(long directorId, String sortBy) {
        return filmStorage.getDirectorFilmsSorted(directorId, sortBy);
    }
}