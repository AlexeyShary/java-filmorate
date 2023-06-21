package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.likes.LikesDbStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/filmTestData.sql", "/userTestData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FilmServiceTest {

    private final LikesDbStorage likesDbStorage;
    private final FilmService filmService;

    @Test
    void getRecommendationsShouldReturnOneTest() {
        likesDbStorage.addLike(1, 1);
        likesDbStorage.addLike(2, 1);
        likesDbStorage.addLike(1, 2);
        assertEquals(1, filmService.getRecommendations(2).size());
        assertTrue(filmService.getRecommendations(2).contains(filmService.get(2)));
    }

    @Test
    void getRecommendationsShouldReturnNoneTest() {
        likesDbStorage.addLike(1, 1);
        likesDbStorage.addLike(2, 1);
        assertEquals(0, filmService.getRecommendations(1).size());
    }
}
