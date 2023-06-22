package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ReviewLikeStorage {

    void addLike(int reviewId, int userId, boolean isPositive);

    void deleteLike(int reviewId, int userId);
}