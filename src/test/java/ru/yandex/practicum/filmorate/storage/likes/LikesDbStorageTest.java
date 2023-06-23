package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/likesTestData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class LikesDbStorageTest {
    private final LikesDbStorage likesDbStorage;

    @Test
    void addLike() {
        likesDbStorage.addLike(4, 1);

        assertThat(likesDbStorage.getLikedUsersIds(1)
                .containsAll(Arrays.asList(1L, 2L, 3L, 4L)));
    }

    @Test
    void deleteLike() {
        likesDbStorage.deleteLike(2, 1);

        assertThat(likesDbStorage.getLikedUsersIds(1)
                .containsAll(Arrays.asList(1L, 3L)));
    }

    @Test
    void getPopularFilmsIds() {
        List<Long> popularFilmsIds = new ArrayList<>(likesDbStorage.getPopularFilmsIds(2));
        List<Long> expectedIds = Arrays.asList(2L, 3L);

        Assertions.assertThat(popularFilmsIds).isEqualTo(expectedIds);
    }
    @Test
    void getFilmsIdsByGenreAndYear(){
        List<Long> filmsIdsByGenreAndYear = new ArrayList<>(likesDbStorage
                .getFilmsIdsByGenreAndYear(5,4L,2014));
        List<Long> expectedIds = Arrays.asList(3L, 1L);
        Assertions.assertThat(filmsIdsByGenreAndYear).isEqualTo(expectedIds);
    }

    @Test
    void getLikedUsersIds() {
        assertThat(likesDbStorage.getLikedUsersIds(1)
                .containsAll(Arrays.asList(1L, 2L, 3L)));
    }

    @Test
    void getCommonFilmsIds() {
        assertThat(likesDbStorage.getCommonFilmsIds(1, 2)
                .containsAll(Arrays.asList(2L, 3L)));
    }
}