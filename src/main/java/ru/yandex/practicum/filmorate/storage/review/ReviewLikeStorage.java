package ru.yandex.practicum.filmorate.storage.review;
public interface ReviewLikeStorage {

    void addLike(int reviewId, int userId, boolean isPositive);

    void deleteLike(int reviewId, int userId);
}