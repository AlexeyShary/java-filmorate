package ru.yandex.practicum.filmorate.storage.likes;

import java.util.Collection;

public interface LikesStorage {
    void addLike(long userId, long filmId);

    void deleteLike(long userId, long filmId);

    Collection<Long> getPopularFilmsIds(long count);

    Collection<Long> getLikedUsersIds(long filmId);
}
