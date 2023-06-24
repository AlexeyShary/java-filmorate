package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewLikeDbStorage implements ReviewLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(long reviewId, long userId, boolean isPositive) {
        String q = "INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID, IS_POSITIVE) " +
                "VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(q, reviewId, userId, isPositive);
            log.info("Добавлен like в БД REVIEW_LIKES.");
        } catch (DuplicateKeyException o) {
            log.error("Получена ошибка повторного ключа.");
            throw new IncorrectIdException("Отзыв уже лайкнут повторно невозможно выполнить действие.");
        }
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        String q = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(q, reviewId, userId);
        log.info("Удален like из БД REVIEW_LIKES");
    }
}