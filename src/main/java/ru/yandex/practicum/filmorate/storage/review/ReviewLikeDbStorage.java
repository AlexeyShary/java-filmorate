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
    public void addLike(int reviewId, int userId, boolean isPositive) {
        String q = "INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID, IS_POSITIVE) " +
                "VALUES (?, ?, ?)";
        log.info("Начат запрос в БД добавление like review.");
        if ((!haveReview(reviewId)) || (!haveUser(userId))) {
            log.error("Ошибка запроса к БД пользователя или отзыва не существует.");
            throw new IncorrectIdException("Данные для добавления like не найдены.");
        }
        try {
            jdbcTemplate.update(q, reviewId, userId, isPositive);
            log.info("Добавлен like в БД REVIEW_LIKES.");
        } catch (DuplicateKeyException o) {
            log.error("Получена ошибка повторного ключа.");
            throw new IncorrectIdException("Отзыв уже лайкнут повторно невозможно выполнить действие.");
        }
    }

    @Override
    public void deleteLike(int reviewId, int userId) {
        String q = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
        log.info("Начат запрос в БД удаления like review.");
        if ((!haveReview(reviewId)) || (!haveUser(userId))) {
            log.error("Ошибка запроса к БД пользователя или отзыва не существует.");
            throw new IncorrectIdException("Данные для удаления like не найдены.");
        }
        jdbcTemplate.update(q, reviewId, userId);
        log.info("Удален like из БД REVIEW_LIKES");
    }

    public boolean haveUser(int userId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM USERS WHERE USER_ID = ?);";
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, userId);
        if (result != null) {
            return result;
        } else {
            return false;
        }
    }

    public boolean haveReview(int reviewId) {
        String q = "SELECT EXISTS(SELECT 1 FROM REVIEWS WHERE REVIEW_ID = ?)";
        Boolean result = jdbcTemplate.queryForObject(q, Boolean.class, reviewId);
        if (result != null) {
            return result;
        } else {
            return false;
        }
    }
}