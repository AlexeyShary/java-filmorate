package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    @Override
    public Collection<Film> getAll() {
        String q = "SELECT FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID" +
                " FROM FILMS";
        return jdbcTemplate.query(q, new FilmMapper());
    }

    @Override
    public Film get(long id) {
        String q = "SELECT FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID" +
                " FROM FILMS WHERE FILM_ID = ?";
        try {
            return jdbcTemplate.queryForObject(q, new FilmMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Ошибка про запросе - фильм с ID {} не найден", id);
            throw new IncorrectIdException("Не найден фильм с ID " + id);
        }
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("FILM_NAME", film.getName());
        parameters.put("DESCRIPTION", film.getDescription());
        parameters.put("RELEASE_DATE", film.getReleaseDate());
        parameters.put("DURATION", film.getDuration());
        parameters.put("MPA_ID", film.getMpa().getId());

        Number filmId = jdbcInsert.executeAndReturnKey(parameters);

        film.setId(filmId.longValue());

        updateGenresSubtable(film);

        return film;
    }

    @Override
    public void delete(long id) {
        String q = "DELETE FROM FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(q, id);
    }

    @Override
    public Film update(Film film) {
        try {
            jdbcTemplate.queryForObject("SELECT FILM_ID FROM FILMS WHERE FILM_ID = ?", Long.class, film.getId());
        } catch (EmptyResultDataAccessException e) {
            log.warn("Ошибка про обновлении - фильм с ID {} не найден", film.getId());
            throw new IncorrectIdException("Не найден фильм с ID " + film.getId());
        }

        String updateQuery = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";

        jdbcTemplate.update(updateQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        updateGenresSubtable(film);
        updateLikesSubtable(film);

        return film;
    }

    private void updateGenresSubtable(Film film) {
        String deleteGenresQuery = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(deleteGenresQuery, film.getId());

        String insertGenresQuery = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(insertGenresQuery, film.getId(), genre.getId());
        }
    }

    private void updateLikesSubtable(Film film) {
        String deleteLikesQuery = "DELETE FROM USERS_FILMS_LIKES WHERE FILM_ID = ?";
        jdbcTemplate.update(deleteLikesQuery, film.getId());

        String insertLikesQuery = "INSERT INTO USERS_FILMS_LIKES (USER_ID, FILM_ID) VALUES (?, ?)";
        for (Long likedUserId : film.getLikedUsersIds()) {
            jdbcTemplate.update(insertLikesQuery, likedUserId, film.getId());
        }
    }

    private class FilmMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            film.setId(rs.getLong("FILM_ID"));
            film.setName(rs.getString("FILM_NAME"));
            film.setDescription(rs.getString("DESCRIPTION"));
            film.setReleaseDate(rs.getDate("RELEASE_DATE").toLocalDate());
            film.setDuration(rs.getInt("DURATION"));
            film.setMpa(mpaDbStorage.get(rs.getLong("MPA_ID")));

            String likesQuery = "SELECT USER_ID FROM USERS_FILMS_LIKES WHERE FILM_ID = ?";
            List<Long> likedUsersIds = jdbcTemplate.queryForList(likesQuery, Long.class, film.getId());
            film.getLikedUsersIds().addAll(likedUsersIds);

            String genresQuery = "SELECT GENRE_ID FROM FILMS_GENRES WHERE FILM_ID = ?";
            List<Long> genresIds = jdbcTemplate.queryForList(genresQuery, Long.class, film.getId());
            for (Long genreId : genresIds) {
                film.getGenres().add(genreDbStorage.get(genreId));
            }

            return film;
        }
    }
}
