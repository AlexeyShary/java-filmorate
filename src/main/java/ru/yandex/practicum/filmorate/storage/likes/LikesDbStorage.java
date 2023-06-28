package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(long userId, long filmId) {
        String q = "MERGE INTO USERS_FILMS_LIKES (USER_ID, FILM_ID) KEY (USER_ID, FILM_ID) VALUES (?, ?)";
        jdbcTemplate.update(q, userId, filmId);
    }

    @Override
    public void deleteLike(long userId, long filmId) {
        String q = "DELETE FROM USERS_FILMS_LIKES WHERE USER_ID = ? AND FILM_ID = ?";
        jdbcTemplate.update(q, userId, filmId);
    }

    @Override
    public Collection<Long> getPopularFilmsIds(long count) {
        String q = "SELECT F.FILM_ID FROM FILMS F" +
                " LEFT JOIN USERS_FILMS_LIKES UFL ON F.FILM_ID = UFL.FILM_ID" +
                " GROUP BY F.FILM_ID" +
                " ORDER BY COUNT(UFL.USER_ID) DESC" +
                " LIMIT ?";
        return jdbcTemplate.queryForList(q, Long.class, count);
    }

    @Override
    public Collection<Long> getFilmsIdsByGenreAndYear(long count, Long genreId, Integer year) {
        StringBuilder q = new StringBuilder();
        q.append("SELECT F.FILM_ID " +
                "FROM FILMS AS F " +
                "LEFT JOIN FILMS_GENRES AS FG ON F.FILM_ID = FG.FILM_ID " +
                "LEFT JOIN USERS_FILMS_LIKES AS UFL ON F.FILM_ID = UFL.FILM_ID " +
                "WHERE 1=1");

        List<Object> paramsList = new ArrayList<>();
        if (genreId != null) {
            q.append(" AND FG.GENRE_ID = ?");
            paramsList.add(genreId);
        }
        if (year != null) {
            q.append(" AND YEAR(F.RELEASE_DATE) = ?");
            paramsList.add(year);
        }
        q.append(" GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(UFL.USER_ID) DESC " +
                "LIMIT ?");

        paramsList.add(count);

        Object[] paramsArr = paramsList.toArray();

        return jdbcTemplate.queryForList(q.toString(), Long.class, paramsArr);
    }

    @Override
    public Collection<Long> getLikedUsersIds(long filmId) {
        String q = "SELECT USER_ID FROM USERS_FILMS_LIKES WHERE FILM_ID = ?";
        return jdbcTemplate.queryForList(q, Long.class, filmId);
    }

    @Override
    public Collection<Long> getCommonFilmsIds(long userId, long friendId) {
        String q = "SELECT FILM_ID" +
                " FROM USERS_FILMS_LIKES" +
                " WHERE USER_ID = ?" +
                " INTERSECT SELECT FILM_ID" +
                " FROM USERS_FILMS_LIKES" +
                " WHERE USER_ID = ?" +
                " GROUP BY USER_ID, FILM_ID";
        return jdbcTemplate.queryForList(q, Long.class, userId, friendId);
    }
}