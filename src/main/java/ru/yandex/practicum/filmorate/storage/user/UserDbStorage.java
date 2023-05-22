package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> getAll() {
        String q = "SELECT USER_ID, EMAIL, LOGIN, USER_NAME, BIRTHDAY" +
                " FROM USERS";
        return jdbcTemplate.query(q, new UserMapper());
    }

    @Override
    public User get(long id) {
        String q = "SELECT USER_ID, EMAIL, LOGIN, USER_NAME, BIRTHDAY" +
                " FROM USERS WHERE USER_ID = ?";
        try {
            return jdbcTemplate.queryForObject(q, new UserMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Ошибка про запросе - пользователь с ID {} не найден", id);
            throw new IncorrectIdException("Не найден пользователь с ID " + id);
        }
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("EMAIL", user.getEmail());
        parameters.put("LOGIN", user.getLogin());
        parameters.put("USER_NAME", user.getName());
        parameters.put("BIRTHDAY", user.getBirthday());

        Number userId = jdbcInsert.executeAndReturnKey(parameters);

        user.setId(userId.longValue());

        return user;
    }

    @Override
    public void delete(long id) {
        String q = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(q, id);
    }

    @Override
    public User update(User user) {
        try {
            jdbcTemplate.queryForObject("SELECT USER_ID FROM USERS WHERE USER_ID = ?", Long.class, user.getId());
        } catch (EmptyResultDataAccessException e) {
            log.warn("Ошибка про обновлении - пользователь с ID {} не найден", user.getId());
            throw new IncorrectIdException("Не найден пользователь с ID " + user.getId());
        }

        String updateQuery = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, USER_NAME = ?, BIRTHDAY = ? WHERE USER_ID = ?";

        jdbcTemplate.update(updateQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        String deleteQuery = "DELETE FROM USERS_FRIENDSHIP WHERE USER_FROM = ?";
        jdbcTemplate.update(deleteQuery, user.getId());

        String insertQuery = "INSERT INTO USERS_FRIENDSHIP (USER_FROM, USER_TO) VALUES (?, ?)";
        for (Long friendId : user.getFriendsIds()) {
            jdbcTemplate.update(insertQuery, user.getId(), friendId);
        }

        return user;
    }

    private class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("USER_ID"));
            user.setEmail(rs.getString("EMAIL"));
            user.setLogin(rs.getString("LOGIN"));
            user.setName(rs.getString("USER_NAME"));
            user.setBirthday(rs.getDate("BIRTHDAY").toLocalDate());

            String q = "SELECT USER_TO FROM USERS_FRIENDSHIP WHERE USER_FROM = ?";
            List<Long> ids = jdbcTemplate.queryForList(q, Long.class, user.getId());
            user.getFriendsIds().addAll(ids);

            return user;
        }
    }
}