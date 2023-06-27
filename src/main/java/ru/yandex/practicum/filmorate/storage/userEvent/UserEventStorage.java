package ru.yandex.practicum.filmorate.storage.userEvent;

import ru.yandex.practicum.filmorate.model.UserEvent;

import java.util.Collection;

public interface UserEventStorage {
    Collection<UserEvent> getByUser(long userId);

    void create(UserEvent userEvent);
}
