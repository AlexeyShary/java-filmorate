package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Qualifier("mpaDbStorage")
    private final MpaStorage mpaStorage;

    @Qualifier("genreDbStorage")
    private final GenreStorage genreStorage;

    @Qualifier("likesDbStorage")
    private final LikesStorage likesStorage;

    @Override
    public Collection<Film> getAll() {
        String q = "SELECT FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID" +
                " FROM FILMS";
        return jdbcTemplate.query(q, new FilmMapper());
    }

    public Collection<Film> getFilmsByListIds(Collection<Long> filmIds) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        SqlParameterSource namedParameters = new MapSqlParameterSource("filmIds", filmIds);

        String query = "SELECT FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID"
                + " FROM FILMS WHERE film_id IN (:filmIds)";

        return namedParameterJdbcTemplate.query(query, namedParameters, new FilmMapper());
    }

    @Override
    public Film get(long id) {
        String q = "SELECT FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID" +
                " FROM FILMS WHERE FILM_ID = ?";
        try {
            return jdbcTemplate.queryForObject(q, new FilmMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка при запросе фильма с ID {}. " + e.getMessage(), id);
            throw new IncorrectIdException(String.format("Ошибка при запросе фильма с ID {}. " + e.getMessage(), id));
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
        updateDirectorsSubtable(film);

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
            log.error("Ошибка при обновлении фильма с ID {}. " + e.getMessage(), film.getId());
            throw new IncorrectIdException(String.format("Ошибка при обновлении фильма с ID {}. " + e.getMessage(),
                    film.getId()));
        }

        String updateQuery = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?," +
                " DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";

        jdbcTemplate.update(updateQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        updateGenresSubtable(film);
        updateDirectorsSubtable(film);

        return film;
    }

    @Override
    public Collection<Film> getDirectorFilmsSorted(long directorId, String sortBy) {
        String sortByYearQuery = "SELECT f.*, m.MPA_NAME" +
                " FROM FILMS_DIRECTORS fd" +
                " JOIN FILMS f ON fd.FILM_ID = f.FILM_ID" +
                " JOIN MPA m ON f.MPA_ID = m.MPA_ID" +
                " WHERE DIRECTOR_ID = ?" +
                " ORDER BY YEAR(f.RELEASE_DATE)";

        String sortByLikesQuery = "SELECT f.*, m.MPA_NAME," +
                " (SELECT COUNT(*) FROM USERS_FILMS_LIKES ufl WHERE fd.FILM_ID = ufl.FILM_ID) AS LIKES" +
                " FROM FILMS_DIRECTORS fd" +
                " JOIN FILMS f ON fd.FILM_ID = f.FILM_ID" +
                " JOIN MPA m ON f.MPA_ID = m.MPA_ID" +
                " WHERE DIRECTOR_ID = ?" +
                " ORDER BY LIKES DESC";

        Collection<Film> films = new ArrayList<>();

        if (sortBy.equals("year")) {
            films = jdbcTemplate.query(sortByYearQuery, new FilmMapper(), directorId);
        } else if (sortBy.equals("likes")) {
            films = jdbcTemplate.query(sortByLikesQuery, new FilmMapper(), directorId);
        }

        if (films.isEmpty()) {
            throw new IncorrectIdException("Режиссер с ID " + directorId + " не найден.");
        }

        return films;
    }

    private void updateGenresSubtable(Film film) {
        String deleteGenresQuery = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(deleteGenresQuery, film.getId());

        String insertGenresQuery = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(insertGenresQuery, film.getId(), genre.getId());
        }
    }

    private void updateDirectorsSubtable(Film film) {
        String deleteDirectorsQuery = "DELETE FROM FILMS_DIRECTORS WHERE FILM_ID = ?";
        jdbcTemplate.update(deleteDirectorsQuery, film.getId());

        String insertDirectorsQuery = "INSERT INTO FILMS_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES (?, ?)";
        for (Director director : film.getDirectors()) {
            jdbcTemplate.update(insertDirectorsQuery, film.getId(), director.getId());
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
            film.setMpa(mpaStorage.get(rs.getLong("MPA_ID")));
            film.getLikedUsersIds().addAll(likesStorage.getLikedUsersIds(film.getId()));

            String genresQuery = "SELECT GENRE_ID FROM FILMS_GENRES WHERE FILM_ID = ?";
            List<Long> genresIds = jdbcTemplate.queryForList(genresQuery, Long.class, film.getId());
            for (Long genreId : genresIds) {
                film.getGenres().add(genreStorage.get(genreId));
            }

            String directorsQuery = "SELECT d.*" +
                    " FROM DIRECTORS d" +
                    " JOIN FILMS_DIRECTORS fd ON d.DIRECTOR_ID = fd.DIRECTOR_ID" +
                    " WHERE FILM_ID = ?";
            List<Director> directors = jdbcTemplate.query(directorsQuery,
                    new DirectorDbStorage.DirectorMapper(), rs.getLong("FILM_ID"));
            film.setDirectors(directors);

            return film;
        }
    }
}
