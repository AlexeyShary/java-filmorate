package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Director> getAll() {
        String q = "SELECT DIRECTOR_ID, DIRECTOR_NAME" +
                " FROM DIRECTORS";
        return jdbcTemplate.query(q, new DirectorMapper());
    }

    @Override
    public Optional<Director> get(long id) {
        String q = "SELECT DIRECTOR_ID, DIRECTOR_NAME" +
                " FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(q, new DirectorMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка при запросе режиссера с ID {}. " + e.getMessage(), id);
            return Optional.empty();
        }
    }

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("DIRECTOR_ID");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("DIRECTOR_NAME", director.getName());

        Number directorId = jdbcInsert.executeAndReturnKey(parameters);

        director.setId(directorId.longValue());

        return director;
    }

    @Override
    public void delete(long id) {
        String q = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(q, id);
    }

    @Override
    public Director update(Director director) {
        try {
            jdbcTemplate.queryForObject("SELECT DIRECTOR_ID FROM DIRECTORS WHERE DIRECTOR_ID = ?",
                    Long.class, director.getId());
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка при обновления режиссера с ID {}. " + e.getMessage(), director.getId());
            throw new IncorrectIdException(String.format("Ошибка при обновления режиссера с ID {}. " + e.getMessage(),
                    director.getId()));
        }

        String updateQuery = "UPDATE DIRECTORS SET DIRECTOR_NAME = ? WHERE DIRECTOR_ID = ?";

        jdbcTemplate.update(updateQuery,
                director.getName(),
                director.getId());

        return director;
    }

    public static class DirectorMapper implements RowMapper<Director> {
        @Override
        public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
            Director director = new Director();
            director.setId(rs.getLong("DIRECTOR_ID"));
            director.setName(rs.getString("DIRECTOR_NAME"));

            return director;
        }
    }
}
