package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAll();
    User get(long id);
    User create(User user);
    long delete(long id);
    User update(User user);
}
