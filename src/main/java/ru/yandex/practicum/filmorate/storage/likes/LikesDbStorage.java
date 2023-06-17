package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(long userId, long filmId) {
        String q = "INSERT INTO USERS_FILMS_LIKES (USER_ID, FILM_ID) VALUES (?, ?)";
        jdbcTemplate.update(q, userId, filmId);
    }

    @Override
    public void deleteLike(long userId, long filmId) {
        String q = "DELETE FROM USERS_FILMS_LIKES WHERE USER_ID = ? AND FILM_ID = ?";
        jdbcTemplate.update(q, userId, filmId);
    }

    @Override
    public Collection<Long> getPopularFilmsIds(long count) {
        String q = "SELECT F.FILM_ID FROM FILMS F" +
                " LEFT JOIN USERS_FILMS_LIKES UFL ON F.FILM_ID = UFL.FILM_ID" +
                " GROUP BY F.FILM_ID" +
                " ORDER BY COUNT(UFL.USER_ID) DESC" +
                " LIMIT ?";
        return jdbcTemplate.queryForList(q, Long.class, count);
    }

    @Override
    public Collection<Long> getLikedUsersIds(long filmId) {
        String q = "SELECT USER_ID FROM USERS_FILMS_LIKES WHERE FILM_ID = ?";
        return jdbcTemplate.queryForList(q, Long.class, filmId);
    }
}