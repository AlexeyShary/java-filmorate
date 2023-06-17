package ru.yandex.practicum.filmorate.storage.friends;

import java.util.Collection;

public interface FriendsStorage {
    void addToFriends(long userId, long friendId);

    void deleteFromFriends(long userId, long friendId);

    Collection<Long> getFriendsIds(long userId);

    Collection<Long> getCommonFriendsIds(long userId, long friendId);
}
