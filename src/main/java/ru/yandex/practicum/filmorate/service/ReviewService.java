package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExceptionValidate;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Review add(Review review) {
        if((review.getUserId() == null ) || (review.getFilmId() == null)){
            throw new ExceptionValidate("userId или filmId = null");
        }
        filmStorage.get(review.getFilmId());
        userStorage.get(review.getUserId());
        return reviewStorage.add(review);
    }

    public Review update(Review review) {
        reviewStorage.get(review.getReviewId());
        return reviewStorage.update(review);
    }

    public void delete(int reviewId) {
        reviewStorage.haveReview(reviewId);
        reviewStorage.delete(reviewId);
    }

    public Review get(int reviewId) {
        return reviewStorage.get(reviewId);
    }

    public List<Review> getAll(Integer filmId, Integer count) {
        return reviewStorage.getAll(filmId, count);
    }

    public void addLike(int reviewId, int userId, boolean isPositive) {
        reviewStorage.haveReview(reviewId);
        userStorage.get(userId);
        reviewLikeStorage.addLike(reviewId, userId, isPositive);
    }

    public void deleteLike(int reviewId, int userId) {
        reviewStorage.haveReview(reviewId);
        userStorage.get(userId);
        reviewLikeStorage.deleteLike(reviewId, userId);
    }
}