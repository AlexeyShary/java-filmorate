package ru.yandex.practicum.filmorate.storage.likes;

import java.util.Collection;

public interface LikesStorage {
    void addLike(long userId, long filmId);

    void deleteLike(long userId, long filmId);

    Collection<Long> getPopularFilmsIds(long count);

    Collection<Long> getFilmsIdsByGenreAndYear(long count, Long genreId, Integer year);

    Collection<Long> getLikedUsersIds(long filmId);

    Collection<Long> getCommonFilmsIds(long userId, long friendId);
}
