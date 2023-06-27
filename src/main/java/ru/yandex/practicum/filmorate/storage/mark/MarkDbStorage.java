package ru.yandex.practicum.filmorate.storage.mark;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Mark;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

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
        int result = jdbcTemplate.update(q, userId, filmId);

        if (result == 0) {
            throw new IncorrectIdException("Оценка от пользователя " + userId + "  фильму " + filmId + " не существует");
        }
    }

    @Override
    public Collection<Mark> getAllMarksOfFilm(long filmId) {
        String q = "SELECT * FROM FILMS_MARKS WHERE FILM_ID = ?";
        return jdbcTemplate.query(q, new MarkMapper(), filmId);
    }

    private static class MarkMapper implements RowMapper<Mark> {
        @Override
        public Mark mapRow(ResultSet rs, int rowNum) throws SQLException {
            Mark mark = new Mark();
            mark.setUserId(rs.getLong("USER_ID"));
            mark.setFilmId(rs.getLong("FILM_ID"));
            mark.setValue(rs.getInt("MARK_VALUE"));

            return mark;
        }
    }
}
