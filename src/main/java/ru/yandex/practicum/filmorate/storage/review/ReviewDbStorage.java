package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review add(Review review) {
        log.info("Начат запрос к БД на добавление отзыва.");
        String q = "INSERT INTO REVIEWS(CONTENT," +
                " IS_POSITIVE, USER_ID, FILM_ID)" +
                " VALUES(?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pr = connection.prepareStatement(q, new String[]{"REVIEW_ID"});
            pr.setString(1, review.getContent());
            pr.setBoolean(2, review.getIsPositive());
            pr.setLong(3, review.getUserId());
            pr.setLong(4, review.getFilmId());
            return pr;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("В БД добавлен отзыв с id: " + review.getReviewId());
        return get(review.getReviewId());
    }

    @Override
    public Review update(Review review) {
        String q = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ? WHERE REVIEW_ID = ?";
        jdbcTemplate.update(q, review.getContent(), review.getIsPositive(), review.getReviewId());
        log.info("Обновление выполнено.");
        return get(review.getReviewId());
    }

    @Override
    public void delete(long reviewId) {
        String q = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
        jdbcTemplate.update(q, reviewId);
        log.info("Отзыв удален.");
    }

    @Override
    public Review get(long reviewId) {
        String q = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USER_ID, R.FILM_ID, " +
                "(SUM(CASE WHEN RL.IS_POSITIVE = TRUE THEN 1 ELSE 0 END) - " +
                "SUM(CASE WHEN RL.IS_POSITIVE = FALSE THEN 1 ELSE 0 END)) AS USEFUL " +
                "FROM REVIEWS AS R " +
                "LEFT JOIN REVIEW_LIKES AS RL ON R.REVIEW_ID = RL.REVIEW_ID " +
                "WHERE R.REVIEW_ID = ? " +
                "GROUP BY R.REVIEW_ID";
        try {
            return jdbcTemplate.queryForObject(q, new ReviewMapper(), reviewId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка при запросе review с ID {}. " + e.getMessage(), reviewId);
            throw new IncorrectIdException(String.format("Ошибка при запросе review с ID {}. " + e.getMessage(), reviewId));
        }
    }

    @Override
    public List<Review> getAll(Integer filmId, Integer count) {
        String q = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USER_ID, R.FILM_ID, " +
                "(SUM(CASE WHEN RL.IS_POSITIVE = TRUE THEN 1 ELSE 0 END) - " +
                "SUM(CASE WHEN RL.IS_POSITIVE = FALSE THEN 1 ELSE 0 END)) AS USEFUL " +
                "FROM REVIEWS AS R " +
                "LEFT JOIN REVIEW_LIKES AS RL ON R.REVIEW_ID = RL.REVIEW_ID " +
                "%s" +
                "GROUP BY R.REVIEW_ID " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";
        String fullQuery;
        if (filmId != null) {
            log.info("Получаем все отзывы у фильма с id: " + filmId);
            fullQuery = String.format(q, "WHERE R.FILM_ID = " + filmId);
        } else {
            log.info("filmId = null! Получаем из БД все отзывы.");
            fullQuery = String.format(q, "");
        }
        return jdbcTemplate.query(fullQuery, new ReviewMapper(), count);
    }

    @Override
    public void haveReview(long reviewId) {
        String q = "SELECT EXISTS(SELECT 1 FROM REVIEWS WHERE REVIEW_ID = ?)";
        Boolean result = jdbcTemplate.queryForObject(q, Boolean.class, reviewId);
        if (result == null) {
            throw new IncorrectIdException("Невозможно удалить несуществующий отзыв.");
        }
    }

    private class ReviewMapper implements RowMapper<Review> {
        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            Review review = new Review();
            review.setReviewId(rs.getInt("review_Id"));
            review.setContent(rs.getString("content"));
            review.setIsPositive(rs.getBoolean("is_positive"));
            review.setUserId(rs.getLong("user_id"));
            review.setFilmId(rs.getLong("film_id"));
            review.setUseful(rs.getLong("useful"));
            log.info("Объект review создан и отправлен обратно.");
            return review;
        }
    }
}