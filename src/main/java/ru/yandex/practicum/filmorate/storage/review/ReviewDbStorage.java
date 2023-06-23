package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ExceptionValidate;
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
    public Review addReview(Review review) {
        log.info("Начат запрос к БД на добавление отзыва.");
        String q = "INSERT INTO REVIEWS(CONTENT," +
                " IS_POSITIVE, USER_ID, FILM_ID)" +
                " VALUES(?, ?, ?, ?);";
        Integer userId = review.getUserId();
        Integer filmId = review.getFilmId();
        if (((!haveFilm(review.getFilmId()))) || (!haveUser(review.getUserId()))) {
            if ((filmId > 0 && userId > 0) || (filmId < 0 || userId < 0)) {
                log.error("При запросе к БД получили ошибку так как пользователя или фильма не существует.");
                throw new IncorrectIdException("Невозможно создать отзыв несуществующим элементам.");
            } else {
                log.error("При запросе к БД получили ошибку так как пользователь или фильм равен null.");
                throw new ExceptionValidate("Пользователь или фильм не могут быть null.");
            }
        }
        if(review.getIsPositive() == null){
            log.error("При запросе к БД получили ошибку так как isPositive null.");
            throw new ExceptionValidate("isPositive не могут быть null.");
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pr = connection.prepareStatement(q, new String[]{"REVIEW_ID"});
            pr.setString(1, review.getContent());
            pr.setBoolean(2, review.getIsPositive());
            pr.setInt(3, review.getUserId());
            pr.setInt(4, review.getFilmId());
            return pr;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("В БД добавлен отзыв с id: " + review.getReviewId());
        return get(review.getReviewId());
    }

    @Override
    public Review update(Review review) {
        String q = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ? WHERE REVIEW_ID = ?";
        log.info("Начат запрос к БД на обновление отзыва.");
        if (!haveReview(review.getReviewId())) {
            log.error("При запросе к БД получили ошибку так как отзыв не существует.");
            return review;
        }
        jdbcTemplate.update(q, review.getContent(), review.getIsPositive(), review.getReviewId());
        log.info("Обновление выполнено.");
        return get(review.getReviewId());
    }

    @Override
    public void delete(int reviewId) {
        String q = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
        log.info("Начат запрос к БД на удаление отзыва.");
        if (!haveReview(reviewId)) {
            log.error("При запросе к БД получили ошибку так как отзыв не существует.");
            throw new IncorrectIdException("Невозможно удалить несуществующий отзыв.");
        }
        jdbcTemplate.update(q, reviewId);
        log.info("Отзыв удален.");
    }

    @Override
    public Review get(int reviewId) {
        String q = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USER_ID, R.FILM_ID, " +
                "(SUM(CASE WHEN RL.IS_POSITIVE = TRUE THEN 1 ELSE 0 END) - " +
                "SUM(CASE WHEN RL.IS_POSITIVE = FALSE THEN 1 ELSE 0 END)) AS USEFUL " +
                "FROM REVIEWS AS R " +
                "LEFT JOIN REVIEW_LIKES AS RL ON R.REVIEW_ID = RL.REVIEW_ID " +
                "WHERE R.REVIEW_ID = ? " +
                "GROUP BY R.REVIEW_ID";
        log.info("Начат запрос к БД на получение отзыва по id.");
        if (!haveReview(reviewId)) {
            log.error("При запросе к БД получили ошибку так как отзыв не существует.");
            throw new IncorrectIdException("Невозможно получить несуществующий отзыв.");
        }
        log.info("Отправлен запрос к БД на создание отзыва по id: " + reviewId);
        return jdbcTemplate.queryForObject(q, this::createReview, reviewId);
    }

    @Override
    public List<Review> getAll(Integer filmId, Integer count) {
        if (filmId == null) {
            log.info("filmId = null! Получаем из БД все отзывы.");
            String q = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USER_ID, R.FILM_ID, " +
                    "(SUM(CASE WHEN RL.IS_POSITIVE = TRUE THEN 1 ELSE 0 END) - " +
                    "SUM(CASE WHEN RL.IS_POSITIVE = FALSE THEN 1 ELSE 0 END)) AS USEFUL " +
                    "FROM REVIEWS AS R " +
                    "LEFT JOIN REVIEW_LIKES AS RL ON R.REVIEW_ID = RL.REVIEW_ID " +
                    "GROUP BY R.REVIEW_ID " +
                    "ORDER BY USEFUL DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(q, this::createReview, count);
        } else if (!haveFilm(filmId)) {
            log.error("При запросе к БД получили ошибку так как фильма не существует.");
            throw new IncorrectIdException("Невозможно создать отзыв несуществующим элементам.");
        } else {
            log.info("Получаем все отзывы у фильма с id: " + filmId);
            String q = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USER_ID, R.FILM_ID, " +
                    "(SUM(CASE WHEN RL.IS_POSITIVE = TRUE THEN 1 ELSE 0 END) - " +
                    "SUM(CASE WHEN RL.IS_POSITIVE = FALSE THEN 1 ELSE 0 END)) AS USEFUL " +
                    "FROM REVIEWS AS R " +
                    "LEFT JOIN REVIEW_LIKES AS RL ON R.REVIEW_ID = RL.REVIEW_ID " +
                    "WHERE R.FILM_ID = ? " +
                    "GROUP BY R.REVIEW_ID " +
                    "ORDER BY USEFUL DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(q, this::createReview, filmId, count);
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

    public boolean haveFilm(Integer id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM FILMS WHERE FILM_ID = ?);";
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        if (result != null) {
            return result;
        } else {
            return false;
        }
    }

    public boolean haveUser(Integer id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM USERS WHERE USER_ID = ?);";
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        if (result != null) {
            return result;
        } else {
            return false;
        }
    }

    public Review createReview(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("review_Id"));
        review.setContent(rs.getString("content"));
        review.setIsPositive(rs.getBoolean("is_positive"));
        review.setUserId(rs.getInt("user_id"));
        review.setFilmId(rs.getInt("film_id"));
        review.setUseful(rs.getInt("useful"));
        log.info("Объект review создан и отправлен обратно.");
        return review;
    }
}