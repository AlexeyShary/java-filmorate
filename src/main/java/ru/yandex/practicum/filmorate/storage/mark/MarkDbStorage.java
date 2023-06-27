package ru.yandex.practicum.filmorate.storage.mark;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarkDbStorage implements MarkStorage {
    private final JdbcTemplate jdbcTemplate;


    @Override
    public void addMark(long userId, long filmId, int value) {
        String q = "INSERT INTO FILMS_MARKS (USER_ID, FILM_ID, MARK_VALUE) VALUES (?, ?, ?)";
        jdbcTemplate.update(q, userId, filmId, value);
    }

    @Override
    public void deleteMark(long userId, long filmId) {
        String q = "DELETE FROM FILMS_MARKS WHERE USER_ID = ? AND FILM_ID = ?";
        jdbcTemplate.update(q, userId, filmId);
    }
}
