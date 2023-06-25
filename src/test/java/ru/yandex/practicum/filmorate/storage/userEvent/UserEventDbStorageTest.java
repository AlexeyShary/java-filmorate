package ru.yandex.practicum.filmorate.storage.userEvent;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/userEventTestData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserEventDbStorageTest {
    private final UserEventDbStorage userEventDbStorage;

    @Test
    void getByUser() {
        assertThat(userEventDbStorage.getByUser(1)).hasSize(2);
    }

    @Test
    void create() {
        UserEvent userEvent = new UserEvent();

        userEvent.setUserId(1);
        userEvent.setEntityId(2);
        userEvent.setEventType(UserEvent.EventType.LIKE);
        userEvent.setOperation(UserEvent.EventOperation.ADD);
        userEvent.setTimestamp(Instant.now().toEpochMilli());

        userEventDbStorage.create(userEvent);

        assertThat(userEventDbStorage.getByUser(1)).hasSize(3);
    }
}