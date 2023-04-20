package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class UserService {
    private int id = 1;
    private final Map<Integer, User> users = new HashMap<>();

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(id++);
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь ID {}", user.getId());
        return user;
    }

    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Попытка обновить пользователя с ID {}, которого не существует", user.getId());
            throw new ValidationException();
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.debug("Обновлен пользователь ID {}", user.getId());
        return user;
    }
}