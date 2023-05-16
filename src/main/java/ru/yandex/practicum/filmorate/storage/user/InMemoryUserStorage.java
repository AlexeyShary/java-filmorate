package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int id = 1;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь ID {}", user.getId());
        return user;
    }

    @Override
    public long delete(long id) {
        if (!users.containsKey(id)) {
            log.warn("Ошибка про удалении - пользователь с ID {} не найден", id);
            throw new IncorrectIdException("Не найден пользователь с ID " + id);
        }

        users.remove(id);
        log.debug("Удален пользователь ID {}", id);
        return id;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Ошибка про обновлении - пользователь с ID {} не найден", user.getId());
            throw new IncorrectIdException("Не найден пользователь с ID " + user.getId());
        }

        users.put(user.getId(), user);
        log.debug("Обновлен пользователь ID {}", user.getId());
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User get(long id) {
        if (!users.containsKey(id)) {
            log.warn("Ошибка про запросе - пользователь с ID {} не найден", id);
            throw new IncorrectIdException("Не найден пользователь с ID " + id);
        }

        return users.get(id);
    }
}