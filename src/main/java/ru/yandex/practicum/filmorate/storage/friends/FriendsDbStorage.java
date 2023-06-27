package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;

import java.util.Collection;
import java.util.List;

@Component
@Slf4j
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
        String q = "SELECT user_id " +
                "FROM users WHERE user_id = ?";
        try {
            jdbcTemplate.queryForObject(q, Long.class, userId);
        } catch (EmptyResultDataAccessException exp) {
            log.error("Не найдены пользователь с ID {} " + exp.getMessage(), userId);
            throw new IncorrectIdException("Пользователь с id" + userId + "не найден");
        }
        String qFriends = "SELECT USER_TO FROM USERS_FRIENDSHIP WHERE USER_FROM = ?";
        return jdbcTemplate.queryForList(qFriends, Long.class, userId);
    }

    @Override
    public Collection<Long> getCommonFriendsIds(long userId, long friendId) {
        String q = "SELECT USER_ID FROM USERS WHERE USER_ID IN (?, ?)";
        List<Long> usersId = jdbcTemplate.queryForList(q, Long.class, userId, friendId);

        if (!usersId.contains(userId) || !usersId.contains(friendId)) {
            log.error("Не найдены пользователи с ID {} или {} . ", userId, friendId);
            throw new IncorrectIdException("Не найден пользователь с ID " + userId + " или " + friendId);
        }

        String qFriends = "SELECT USER_TO FROM USERS_FRIENDSHIP WHERE USER_FROM = ? AND USER_TO IN " +
                "(SELECT USER_TO FROM USERS_FRIENDSHIP WHERE USER_FROM = ?)";
        return jdbcTemplate.queryForList(qFriends, Long.class, userId, friendId);
    }
}
