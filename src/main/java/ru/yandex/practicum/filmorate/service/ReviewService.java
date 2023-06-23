package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;

    public Review addReviews(Review review) {
        log.info("Сервис отправляет запрос в БД REVIEWS на добавление отзыва.");
        return reviewStorage.addReview(review);
    }

    public Review update(Review review) {
        log.info("Сервис отправляет запрос в БД REVIEWS на обновление отзыва.");
        return reviewStorage.update(review);
    }

    public void delete(int reviewId) {
        log.info("Сервис отправляет запрос в БД REVIEWS на удаление отзыва.");
        reviewStorage.delete(reviewId);
    }

    public Review get(int reviewId) {
        log.info("Сервис отправляет запрос в БД REVIEWS на получение отзыва по id: " + reviewId);
        return reviewStorage.get(reviewId);
    }

    public List<Review> getAll(Integer filmId, Integer count) {
        log.info("Сервис отправляет запрос в БД REVIEWS на получение всех отзывов.");
        return reviewStorage.getAll(filmId, count);
    }

    public void addLike(int reviewId, int userId, boolean isPositive) {
        log.info("Сервис отправляет запрос в БД REVIEW_LIKES на добавление like или dislike.");
        reviewLikeStorage.addLike(reviewId, userId, isPositive);
    }

    public void deleteLike(int reviewId, int userId) {
        log.info("Сервис отправляет запрос в БД REVIEW_LIKES на удаление like или dislike.");
        reviewLikeStorage.deleteLike(reviewId, userId);
    }
}