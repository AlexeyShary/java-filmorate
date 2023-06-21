package ru.yandex.practicum.filmorate.storage.userEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventDbStorage implements UserEventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<UserEvent> getByUser(long userId) {
        String q = "SELECT ue.EVENT_ID, ue.USER_ID, ue.ENTITY_ID, et.EVENT_TYPE_NAME, " +
                "eo.EVENT_OPERATION_NAME, ue.EVENT_TIMESTAMP " +
                "FROM USERS_EVENTS ue " +
                "JOIN EVENTS_TYPES et ON ue.EVENT_TYPE_ID = et.EVENT_TYPE_ID " +
                "JOIN EVENTS_OPERATIONS eo ON ue.EVENT_OPERATION_ID = eo.EVENT_OPERATION_ID " +
                "WHERE ue.USER_ID = ?";

        return jdbcTemplate.query(q, new UserEventMapper(), userId);
    }

    @Override
    public void create(UserEvent userEvent) {
        String q = "INSERT INTO USERS_EVENTS (USER_ID, ENTITY_ID, EVENT_TYPE_ID, EVENT_OPERATION_ID, EVENT_TIMESTAMP) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(q, userEvent.getUserId(), userEvent.getEntityId(),
                userEvent.getEventType().ordinal() + 1, userEvent.getOperation().ordinal() + 1, userEvent.getTimestamp());
    }

    private static class UserEventMapper implements RowMapper<UserEvent> {
        @Override
        public UserEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserEvent userEvent = new UserEvent();
            userEvent.setEventId(rs.getLong("EVENT_ID"));
            userEvent.setUserId(rs.getLong("USER_ID"));
            userEvent.setEntityId(rs.getLong("ENTITY_ID"));
            userEvent.setEventType(UserEvent.EventType.valueOf(rs.getString("EVENT_TYPE_NAME")));
            userEvent.setOperation(UserEvent.EventOperation.valueOf(rs.getString("EVENT_OPERATION_NAME")));
            userEvent.setTimestamp(rs.getLong("EVENT_TIMESTAMP"));

            return userEvent;
        }
    }
}
