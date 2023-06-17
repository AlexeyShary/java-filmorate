package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/friendsTestData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FriendsDbStorageTest {
    private final FriendsDbStorage friendsDbStorage;

    @Test
    void addToFriends() {
        friendsDbStorage.addToFriends(1, 4);
        assertThat(friendsDbStorage.getFriendsIds(1)).hasSize(3);
    }

    @Test
    void deleteFromFriends() {
        friendsDbStorage.deleteFromFriends(1, 2);
        assertThat(friendsDbStorage.getFriendsIds(1)).hasSize(1);
    }

    @Test
    void getFriendsIds() {
        assertThat(friendsDbStorage.getFriendsIds(1)).hasSize(2);
    }

    @Test
    void getCommonFriendsIds() {
        assertThat(friendsDbStorage.getCommonFriendsIds(1, 2)).hasSize(1);
    }
}