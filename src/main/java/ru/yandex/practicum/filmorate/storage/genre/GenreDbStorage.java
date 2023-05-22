package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Slf4j
@Component("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> getAll() {
        String q = "SELECT GENRE_ID, GENRE_NAME" +
                " FROM GENRES";
        return jdbcTemplate.query(q, new GenreMapper());
    }

    @Override
    public Genre get(long id) {
        String q = "SELECT GENRE_ID, GENRE_NAME" +
                " FROM GENRES WHERE GENRE_ID = ?";
        try {
            return jdbcTemplate.queryForObject(q, new GenreDbStorage.GenreMapper(), id);
        }
        catch (EmptyResultDataAccessException e) {
            log.warn("Ошибка про запросе - жанр с ID {} не найден", id);
            throw new IncorrectIdException("Не найден жанр с ID " + id);
        }
    }

    private class GenreMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            Genre genre = new Genre();
            genre.setId(rs.getLong("GENRE_ID"));
            genre.setName(rs.getString("GENRE_NAME"));

            return genre;
        }
    }
}
