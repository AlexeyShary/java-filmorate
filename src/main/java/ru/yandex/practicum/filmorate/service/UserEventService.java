package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.userEvent.UserEventStorage;

import java.time.Instant;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventService {
    @Qualifier("userEventDbStorage")
    private final UserEventStorage userEventStorage;

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public Collection<UserEvent> getByUser(long userId) {
        User user = userStorage.get(userId);
        return userEventStorage.getByUser(userId);
    }

    public void create(long userId, long entityId, UserEvent.EventType eventType, UserEvent.EventOperation eventOperation) {
        UserEvent userEvent = new UserEvent();

        userEvent.setUserId(userId);
        userEvent.setEntityId(entityId);
        userEvent.setEventType(eventType);
        userEvent.setOperation(eventOperation);
        userEvent.setTimestamp(Instant.now().toEpochMilli());

        userEventStorage.create(userEvent);
    }
}