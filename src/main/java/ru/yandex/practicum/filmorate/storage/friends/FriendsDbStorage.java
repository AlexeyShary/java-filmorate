package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addToFriends(long userId, long friendId) {
        String q = "INSERT INTO USERS_FRIENDSHIP (USER_FROM, USER_TO) VALUES (?, ?)";
        jdbcTemplate.update(q, userId, friendId);
    }

    @Override
    public void deleteFromFriends(long userId, long friendId) {
        String q = "DELETE FROM USERS_FRIENDSHIP WHERE USER_FROM = ? AND USER_TO = ?";
        jdbcTemplate.update(q, userId, friendId);
    }

    @Override
    public Collection<Long> getFriendsIds(long userId) {
        String q = "SELECT USER_TO FROM USERS_FRIENDSHIP WHERE USER_FROM = ?";
        return jdbcTemplate.queryForList(q, Long.class, userId);
    }

    @Override
    public Collection<Long> getCommonFriendsIds(long userId, long friendId) {
        String q = "SELECT USER_TO FROM USERS_FRIENDSHIP WHERE USER_FROM = ? AND USER_TO IN " +
                "(SELECT USER_TO FROM USERS_FRIENDSHIP WHERE USER_FROM = ?)";
        return jdbcTemplate.queryForList(q, Long.class, userId, friendId);
    }
}
