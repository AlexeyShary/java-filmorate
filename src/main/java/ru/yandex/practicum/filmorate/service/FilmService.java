package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.mark.MarkStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
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

    @Qualifier("userEventService")
    private final UserEventService userEventService;

    @Qualifier("markDbStorage")
    private final MarkStorage markStorage;

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

    public Collection<Film> getSearchResult(String searchTerm, FilmSearchByMode by) {
        return filmStorage.getSearchResult(searchTerm, by);
    }

    public Collection<Film> getPopular(long count, Long genreId, Integer year) {
        if (genreId == null && year == null) {
            Collection<Long> popular = likesStorage.getPopularFilmsIds(count);
            return filmStorage.getFilmsByListIds(popular);
        } else {
            Collection<Long> popularWithGenreAndYear = likesStorage.getFilmsIdsByGenreAndYear(count, genreId, year);
            return filmStorage.getFilmsByListIds(popularWithGenreAndYear);
        }
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
        userEventService.create(userId, id, UserEvent.EventType.LIKE, UserEvent.EventOperation.ADD);

        log.debug("Добавлен лайк фильму {} от пользователя {}", id, userId);
    }

    public void addMark(long id, long userId, int value) {
        Film film = get(id);
        User user = userStorage.get(userId);

        film.getMarks().put(userId, value);

        markStorage.addMark(userId, id, value);
        userEventService.create(userId, id, UserEvent.EventType.MARK, UserEvent.EventOperation.ADD);

        log.debug("Добавлена оценка {} фильму {} от пользователя {}", value, id, userId);
    }

    public void deleteLike(long id, long userId) {
        Film film = get(id);
        User user = userStorage.get(userId);

        if (!film.getLikedUsersIds().contains(userId)) {
            log.warn("Ошибка про удалении лайка с фильма - пользователь с ID {} не лайкал фильм {}", userId, id);
            throw new IncorrectIdException("Пользователь с ID " + userId + " не лайкал фильм " + id);
        }

        likesStorage.deleteLike(userId, id);
        userEventService.create(userId, id, UserEvent.EventType.LIKE, UserEvent.EventOperation.REMOVE);

        log.debug("Удален лайк фильму {} от пользователя {}", id, userId);
    }

    public void deleteMark(long id, long userId) {
        Film film = get(id);
        User user = userStorage.get(userId);

        if (!film.getMarks().containsKey(userId)) {
            log.warn("Ошибка про удалении оценки с фильма - пользователь с ID {} не оценивал фильм {}", userId, id);
            throw new IncorrectIdException("Пользователь с ID " + userId + " не оценивал фильм " + id);
        }

        markStorage.deleteMark(userId, id);
        userEventService.create(userId, id, UserEvent.EventType.MARK, UserEvent.EventOperation.REMOVE);

        log.debug("Удалена оценка фильму {} от пользователя {}", id, userId);
    }

    public Set<Film> getRecommendations(long userId) {
        Map<Long, List<Long>> filmsOfUsers = new HashMap<>();
        Collection<User> allUsers = userStorage.getAll();
        for (User user : allUsers) {
            filmsOfUsers.put(user.getId(), filmStorage.getUsersLikedFilmsIds(user.getId()));
        }
        long maxIntersection = 0;
        Set<Long> intersection = new HashSet<>();
        for (Long id : filmsOfUsers.keySet()) {
            if (id == userId) continue;

            long numOfIntersection = filmsOfUsers.get(id).stream()
                    .filter(filmId -> filmsOfUsers.get(userId).contains(filmId)).count();

            if (numOfIntersection == maxIntersection & numOfIntersection != 0) {
                intersection.add(id);
            }

            if (numOfIntersection > maxIntersection) {
                maxIntersection = numOfIntersection;
                intersection = new HashSet<>();
                intersection.add(id);
            }
        }
        if (maxIntersection == 0) return new HashSet<>();
        else return intersection.stream().flatMap(idUser -> filmStorage.getUsersLikedFilmsIds(idUser).stream())
                .filter(filmId -> !filmsOfUsers.get(userId).contains(filmId))
                .map(filmStorage::get)
                .collect(Collectors.toSet());
    }

    public Collection<Film> getDirectorFilmsSorted(long directorId, FilmSortByMode sortBy) {
        Collection<Film> films = filmStorage.getDirectorFilmsSorted(directorId, sortBy);

        if (films.isEmpty()) {
            throw new IncorrectIdException("Режиссер с ID " + directorId + " не найден.");
        }

        return films;
    }
}