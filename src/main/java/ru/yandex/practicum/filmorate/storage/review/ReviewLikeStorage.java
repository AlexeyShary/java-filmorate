package ru.yandex.practicum.filmorate.storage.review;
public interface ReviewLikeStorage {

    void addLike(long reviewId, long userId, boolean isPositive);

    void deleteLike(long reviewId, long userId);
}