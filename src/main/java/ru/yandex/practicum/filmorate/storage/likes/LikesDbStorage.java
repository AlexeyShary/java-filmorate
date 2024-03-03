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
        String q = "MERGE INTO USERS_FILMS_LIKES (USER_ID, FILM_ID) KEY (USER_ID, FILM_ID) VALUES (?, ?)";
        jdbcTemplate.update(q, userId, filmId);
    }

    @Override
    public void deleteLike(long userId, long filmId) {
        String q = "DELETE FROM USERS_FILMS_LIKES WHERE USER_ID = ? AND FILM_ID = ?";
        jdbcTemplate.update(q, userId, filmId);
    }

    @Override
    public Collection<Long> getLikedUsersIds(long filmId) {
        String q = "SELECT USER_ID FROM USERS_FILMS_LIKES WHERE FILM_ID = ?";
        return jdbcTemplate.queryForList(q, Long.class, filmId);
    }

    @Override
    public Collection<Long> getCommonFilmsIds(long userId, long friendId) {
        String q = "SELECT FILM_ID" +
                " FROM USERS_FILMS_LIKES" +
                " WHERE USER_ID = ?" +
                " INTERSECT SELECT FILM_ID" +
                " FROM USERS_FILMS_LIKES" +
                " WHERE USER_ID = ?" +
                " GROUP BY USER_ID, FILM_ID";
        return jdbcTemplate.queryForList(q, Long.class, userId, friendId);
    }
}