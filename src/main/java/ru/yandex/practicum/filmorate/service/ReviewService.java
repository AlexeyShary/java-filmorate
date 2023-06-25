package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExceptionValidate;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.UserEvent;
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
    private final UserEventService userEventService;

    public Review add(Review review) {
        if ((review.getUserId() == null) || (review.getFilmId() == null)) {
            throw new ExceptionValidate("userId или filmId = null");
        }
        filmStorage.get(review.getFilmId());
        userStorage.get(review.getUserId());

        Review result = reviewStorage.add(review);
        userEventService.create(result.getUserId(), result.getReviewId(), UserEvent.EventType.REVIEW, UserEvent.EventOperation.ADD);
        return result;
    }

    public Review update(Review review) {
        reviewStorage.get(review.getReviewId());

        Review result = reviewStorage.update(review);
        userEventService.create(result.getUserId(), result.getReviewId(), UserEvent.EventType.REVIEW, UserEvent.EventOperation.UPDATE);
        return result;
    }

    public void delete(int reviewId) {
        Review review = reviewStorage.get(reviewId);

        reviewStorage.haveReview(reviewId);
        reviewStorage.delete(reviewId);

        userEventService.create(review.getUserId(), review.getReviewId(), UserEvent.EventType.REVIEW, UserEvent.EventOperation.REMOVE);
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