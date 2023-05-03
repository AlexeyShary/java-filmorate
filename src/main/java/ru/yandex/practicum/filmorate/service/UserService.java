package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public long delete(long id) {
        return userStorage.delete(id);
    }

    public void addToFriends(long id, long friendId) {
        if (getAll().stream().noneMatch(user -> user.getId() == id)) {
            log.warn("Ошибка про добавлении пользователей в друзья - пользователь с ID {} не найден", id);
            throw new IncorrectIdException("Не найден пользователь с ID " + id);
        }

        if (getAll().stream().noneMatch(user -> user.getId() == id)) {
            log.warn("Ошибка про добавлении пользователей в друзья - пользователь с ID {} не найден", id);
            throw new IncorrectIdException("Не найден пользователь с ID " + id);
        }

        User user1 = userStorage.get(id);
        User user2 = userStorage.get(friendId);

        user1.getFriendsIds().add(friendId);
        user2.getFriendsIds().add(id);

        log.debug("Пользователи ID {} и {} добавлены в друзья друг к другу", id, friendId);
    }

    public void deleteFromFriends(long id, long friendId) {
        User user1 = userStorage.get(id);
        User user2 = userStorage.get(friendId);

        if (user1.getFriendsIds().stream().noneMatch(uId -> uId == friendId)
                || user2.getFriendsIds().stream().noneMatch(uId -> uId == id)) {
            log.warn("Ошибка про удалении пользователя из друзей - пользователь с ID {} не в друзьях у {}", id, friendId);
            throw new IncorrectIdException("Пользователи ID " + id + " и " + friendId + " не добавлены в друзья");
        }

        user1.getFriendsIds().remove(friendId);
        user2.getFriendsIds().remove(id);

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