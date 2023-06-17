package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/userTestData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;

    @Test
    void getAll() {
        assertThat(userDbStorage.getAll()).hasSize(2);
    }

    @Test
    void get() {
        assertThat(userDbStorage.get(1))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("email", "Matthew@McConaughey.com")
                .hasFieldOrPropertyWithValue("login", "Matt")
                .hasFieldOrPropertyWithValue("name", "Matthew McConaughey")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1969, 11, 4));
    }

    @Test
    void create() {
        User newUser = new User();
        newUser.setEmail("Samara@Morgan.com");
        newUser.setLogin("icu");
        newUser.setName("Samara Morgan");
        newUser.setBirthday(LocalDate.of(1970, 9, 8));

        userDbStorage.create(newUser);

        assertThat(userDbStorage.getAll()).hasSize(3);

        assertThat(userDbStorage.get(3))
                .hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("email", "Samara@Morgan.com")
                .hasFieldOrPropertyWithValue("login", "icu")
                .hasFieldOrPropertyWithValue("name", "Samara Morgan")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1970, 9, 8));
    }

    @Test
    void delete() {
        userDbStorage.delete(1);
        assertThat(userDbStorage.getAll()).hasSize(1);
    }

    @Test
    void update() {
        User user = userDbStorage.get(1);
        user.setLogin("NotMatt");

        userDbStorage.update(user);

        assertThat(userDbStorage.get(1))
                .hasFieldOrPropertyWithValue("login", "NotMatt");
    }
}