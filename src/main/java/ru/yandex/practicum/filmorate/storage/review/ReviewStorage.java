package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review addReview(Review review);

    Review update(Review review);

    void delete(int reviewId);

    Review get(int reviewId);

    List<Review> getAll(Integer filmId, Integer count);
}