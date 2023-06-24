package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review add(Review review);

    Review update(Review review);

    void delete(long reviewId);

    Review get(long reviewId);

    List<Review> getAll(Integer filmId, Integer count);

    void haveReview(long reviewId);
}