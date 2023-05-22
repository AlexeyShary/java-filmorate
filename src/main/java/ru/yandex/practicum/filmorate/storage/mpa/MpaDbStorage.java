package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Slf4j
@Component("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> getAll() {
        String q = "SELECT MPA_ID, MPA_NAME" +
                " FROM MPA";
        return jdbcTemplate.query(q, new MpaMapper());
    }

    @Override
    public Mpa get(long id) {
        String q = "SELECT MPA_ID, MPA_NAME" +
                " FROM MPA WHERE MPA_ID = ?";
        try {
            return jdbcTemplate.queryForObject(q, new MpaMapper(), id);
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Ошибка про запросе - MPA с ID {} не найден", id);
            throw new IncorrectIdException("Не найден MPA с ID " + id);
        }
    }

    private class MpaMapper implements RowMapper<Mpa> {
        @Override
        public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getLong("MPA_ID"));
            mpa.setName(rs.getString("MPA_NAME"));

            return mpa;
        }
    }
}