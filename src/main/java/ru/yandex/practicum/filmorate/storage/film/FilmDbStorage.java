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
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.mark.MarkStorage;
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

    @Qualifier("markDbStorage")
    private final MarkStorage markStorage;

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
    public Collection<Film> getSearchResult(String searchTerm, FilmSearchByMode by) {
        switch (by) {
            case DIRECTOR:
                return findByDirector(searchTerm);
            case TITLE:
                return findByTitle(searchTerm);
            case DIRECTOR_TITLE:
                return findByDirectorAndTitle(searchTerm, searchTerm);
            default:
                throw new IllegalArgumentException("Invalid 'by' parameter");
        }
    }

    private List<Film> findByDirector(String query) {
        String sql = "SELECT f.* FROM FILMS f " +
                "LEFT JOIN FILMS_DIRECTORS fd ON f.FILM_ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE LOWER(d.DIRECTOR_NAME) LIKE ? ";
        String param = "%" + query.toLowerCase() + "%";
        return jdbcTemplate.query(sql, new FilmMapper(), param);
    }

    private List<Film> findByTitle(String query) {
        String sql = "SELECT * FROM FILMS WHERE LOWER(FILM_NAME) LIKE ?";
        String param = "%" + query.toLowerCase() + "%";
        return jdbcTemplate.query(sql, new FilmMapper(), param);
    }

    private List<Film> findByDirectorAndTitle(String directorQuery, String titleQuery) {
        String sql = "SELECT f.* FROM FILMS f " +
                "LEFT JOIN FILMS_DIRECTORS fd ON f.FILM_ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE LOWER(d.DIRECTOR_NAME) LIKE ? OR LOWER(f.FILM_NAME) LIKE ? " +
                "ORDER BY CASE WHEN LOWER(d.DIRECTOR_NAME) LIKE ? THEN 0 ELSE 1 END, " +
                "CASE WHEN LOWER(f.FILM_NAME) LIKE ? THEN 0 ELSE 1 END";
        String directorParam = "%" + directorQuery.toLowerCase() + "%";
        String titleParam = "%" + titleQuery.toLowerCase() + "%";
        return jdbcTemplate.query(sql, new FilmMapper(), directorParam, titleParam, directorParam, titleParam);
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

        String updateQuery = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";

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
    public List<Long> getUsersLikedFilmsIds(long userId) {
        String query = "SELECT FILM_ID FROM USERS_FILMS_LIKES WHERE USER_ID = ?";
        return jdbcTemplate.queryForList(query, Long.class, userId);
    }

    @Override
    public Collection<Film> getDirectorFilmsSorted(long directorId, FilmSortByMode sortBy) {
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

        switch (sortBy) {
            case YEAR:
                return jdbcTemplate.query(sortByYearQuery, new FilmMapper(), directorId);
            case LIKES:
                return jdbcTemplate.query(sortByLikesQuery, new FilmMapper(), directorId);
            default:
                throw new IllegalArgumentException("Передан некорректный параметр сортировки " + sortBy);
        }
    }

    @Override
    public Collection<Film> getPopularFilms(long count) {
        String q = "SELECT F.FILM_ID, F.FILM_NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA_ID," +
                " CASE " +
                    " WHEN COUNT(M.MARK_VALUE) + COUNT(L.USER_ID) = 0 THEN 0" +
                    " WHEN COUNT(M.MARK_VALUE) = 0 THEN 10" +
                    " ELSE (COALESCE(SUM(M.MARK_VALUE), 0) + (COUNT(L.USER_ID) * 10)) / (COUNT(M.MARK_VALUE) + COUNT(L.USER_ID))" +
                " END AS RATING" +
                " FROM FILMS F" +
                " LEFT JOIN FILMS_MARKS M ON F.FILM_ID = M.FILM_ID" +
                " LEFT JOIN USERS_FILMS_LIKES L ON F.FILM_ID = L.FILM_ID" +
                " GROUP BY F.FILM_ID, F.FILM_NAME" +
                " ORDER BY RATING DESC" +
                " LIMIT ?";

        return jdbcTemplate.query(q, new FilmMapper(), count);
    }

    @Override
    public Collection<Film> getPopularFilmsByGenreAndYear(long count, Long genreId, Integer year) {
        String q = "SELECT F.FILM_ID, F.FILM_NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA_ID," +
                " CASE " +
                    " WHEN COUNT(M.MARK_VALUE) + COUNT(L.USER_ID) = 0 THEN 0" +
                    " WHEN COUNT(M.MARK_VALUE) = 0 THEN 10" +
                    " ELSE (COALESCE(SUM(M.MARK_VALUE), 0) + (COUNT(L.USER_ID) * 10)) / (COUNT(M.MARK_VALUE) + COUNT(L.USER_ID))" +
                " END AS RATING" +
                " FROM FILMS F" +
                " INNER JOIN FILMS_GENRES FG ON F.FILM_ID = FG.FILM_ID" +
                " LEFT JOIN FILMS_MARKS M ON F.FILM_ID = M.FILM_ID" +
                " LEFT JOIN USERS_FILMS_LIKES L ON F.FILM_ID = L.FILM_ID" +
                " WHERE 1=1";

        List<Object> paramsList = new ArrayList<>();
        if (genreId != null) {
            q += " AND FG.GENRE_ID = ?";
            paramsList.add(genreId);
        }
        if (year != null) {
            q += " AND YEAR(F.RELEASE_DATE) = ?";
            paramsList.add(year);
        }

        q +=    " GROUP BY F.FILM_ID, F.FILM_NAME" +
                " ORDER BY RATING DESC" +
                " LIMIT ?";

        paramsList.add(count);

        Object[] paramsArr = paramsList.toArray();

        return jdbcTemplate.query(q, new FilmMapper(), paramsArr);
    }

    public Collection<Film> getRecommendations(long userId) {
        String q = "SELECT F.FILM_ID, F.FILM_NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA_ID" +
                " FROM FILMS F" +
                " WHERE F.FILM_ID IN (" +
                    " SELECT UFL.FILM_ID" +
                    " FROM USERS_FILMS_LIKES UFL" +
                    " WHERE UFL.USER_ID = (" +
                        " SELECT UFL.USER_ID FROM USERS_FILMS_LIKES UFL" +
                        " WHERE UFL.USER_ID <> ?" +
                        " AND UFL.FILM_ID IN (" +
                            " SELECT FILM_ID" +
                            " FROM USERS_FILMS_LIKES" +
                            " WHERE USER_ID = ?)" +
                        " GROUP BY UFL.USER_ID" +
                        " ORDER BY COUNT(*) DESC" +
                        " LIMIT 1)" +
                    ")" +
                " AND F.FILM_ID NOT IN (" +
                " SELECT FILM_ID" +
                " FROM USERS_FILMS_LIKES" +
                " WHERE USER_ID = ?)";

        return jdbcTemplate.query(q, new FilmMapper(), userId, userId, userId);
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
            film.getMarks().addAll(markStorage.getAllMarksOfFilm(film.getId()));
            film.setRating();

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
