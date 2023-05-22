package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User get(long id) {
        return userStorage.get(id);
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.create(user);
    }

    public User update(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.update(user);
    }

    public void delete(long id) {
        userStorage.delete(id);
    }

    public void addToFriends(long id, long friendId) {
        if ((getAll().stream().noneMatch(user -> user.getId() == id))
                || (getAll().stream().noneMatch(user -> user.getId() == friendId))) {
            log.warn("Ошибка при добавлении пользователей в друзья - пользователь с ID {} или {} не найден", id, friendId);
            throw new IncorrectIdException("Не найден пользователь с ID " + id + " или" + friendId);
        }

        User user = userStorage.get(id);
        user.getFriendsIds().add(friendId);
        update(user);

        log.debug("Пользователь ID {} добавил в друзья пользователя {}", id, friendId);
    }

    public void deleteFromFriends(long id, long friendId) {
        User user = userStorage.get(id);

        if (user.getFriendsIds().stream().noneMatch(uId -> uId == friendId)) {
            log.warn("Ошибка про удалении пользователя из друзей - пользователь с ID {} не в друзьях у {}", friendId, id);
            throw new IncorrectIdException("Пользователи ID " + id + " и " + friendId + " не добавлены в друзья");
        }

        user.getFriendsIds().remove(friendId);
        update(user);

        log.debug("Пользователи ID {} и {} удалены из друзей друг от друга", id, friendId);
    }

    public Collection<User> getFriends(long id) {
        return userStorage.get(id).getFriendsIds().stream()
                .map(userStorage::get)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(long id, long otherId) {
        Collection<User> commonFriendList = getFriends(id);
        commonFriendList.retainAll(getFriends(otherId));
        return commonFriendList;
    }
}