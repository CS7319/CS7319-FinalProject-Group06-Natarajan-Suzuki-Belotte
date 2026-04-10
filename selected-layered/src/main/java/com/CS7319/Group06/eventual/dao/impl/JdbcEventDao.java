package com.CS7319.Group06.eventual.dao.impl;

import com.CS7319.Group06.eventual.dao.EventDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.Event;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * JDBC implementation of EventDao
 *
 * @author harininatarajan
 */
@Component
public class JdbcEventDao implements EventDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcEventDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Event> eventRowMapper = (rs, rowNum) -> {
        Event event = new Event();
        event.setEventId(rs.getInt("id"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setLocation(rs.getString("location"));
        event.setStartDateTime(rs.getTimestamp("start_datetime").toLocalDateTime());
        event.setEndDateTime(rs.getTimestamp("end_datetime").toLocalDateTime());
        event.setOrganizerEmail(rs.getString("organizer_email"));
        event.setOrganizerName(rs.getString("organizer_name"));
        event.setCapacity(rs.getInt("capacity"));
        event.setEventPicture(rs.getString("event_picture"));
        event.setEventType(rs.getString("event_type"));
        int groupId = rs.getInt("group_id");
        event.setGroupId(rs.wasNull() ? null : groupId);

        Array categoryArray = rs.getArray("category_ids");
        if (categoryArray != null) {
            Integer[] ids = (Integer[]) categoryArray.getArray();
            event.setCategoryIds(new ArrayList<>(Arrays.asList(ids)));
        } else {
            event.setCategoryIds(new ArrayList<>());
        }

        return event;
    };

    @Override
    public Event getEventById(int id) {
        String sql = "SELECT e.id, e.title, e.description, e.location, e.start_datetime, e.end_datetime, " +
                "e.organizer_email, u.name AS organizer_name, " +
                "e.capacity, e.event_picture, e.event_type, e.group_id, e.category_ids " +
                "FROM events e JOIN users u ON e.organizer_email = u.email " +
                "WHERE e.id = ?";
        try {
            List<Event> results = jdbcTemplate.query(sql, eventRowMapper, id);
            return results.isEmpty() ? null : results.getFirst();
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public Event createEvent(Event event) {
        String sql = "INSERT INTO events (title, description, location, start_datetime, end_datetime, organizer_email, " +
                "capacity, event_picture, event_type, group_id, category_ids) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try {
            List<Integer> categoryIds = event.getCategoryIds();
            Integer eventId = jdbcTemplate.execute((java.sql.Connection conn) -> {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, event.getTitle());
                ps.setString(2, event.getDescription());
                ps.setString(3, event.getLocation());
                ps.setObject(4, event.getStartDateTime());
                ps.setObject(5, event.getEndDateTime());
                ps.setString(6, event.getOrganizerEmail());
                ps.setInt(7, event.getCapacity());
                ps.setString(8, event.getEventPicture());
                ps.setString(9, event.getEventType());
                if (event.getGroupId() != null) {
                    ps.setInt(10, event.getGroupId());
                } else {
                    ps.setNull(10, Types.INTEGER);
                }
                ps.setArray(11, conn.createArrayOf("integer",
                        categoryIds != null ? categoryIds.toArray() : new Integer[0]));
                var rs = ps.executeQuery();
                return rs.next() ? rs.getInt(1) : null;
            });
            return getEventById(eventId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public Event updateEvent(Event event) {
        String sql = "UPDATE events SET " +
                "title           = COALESCE(?, title), " +
                "description     = COALESCE(?, description), " +
                "location        = COALESCE(?, location), " +
                "start_datetime  = COALESCE(?, start_datetime), " +
                "end_datetime    = COALESCE(?, end_datetime), " +
                "capacity        = COALESCE(?, capacity), " +
                "event_picture   = COALESCE(?, event_picture), " +
                "event_type      = COALESCE(?, event_type), " +
                "group_id        = COALESCE(?, group_id), " +
                "category_ids    = COALESCE(?, category_ids) " +
                "WHERE id = ?";
        try {
            int rowsAffected = jdbcTemplate.execute((java.sql.Connection conn) -> {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, event.getTitle());
                ps.setString(2, event.getDescription());
                ps.setString(3, event.getLocation());
                ps.setObject(4, event.getStartDateTime());
                ps.setObject(5, event.getEndDateTime());
                // capacity: 0 means "leave unchanged"
                if (event.getCapacity() > 0) {
                    ps.setInt(6, event.getCapacity());
                } else {
                    ps.setNull(6, Types.INTEGER);
                }
                ps.setString(7, event.getEventPicture());
                ps.setString(8, event.getEventType());
                if (event.getGroupId() != null) {
                    ps.setInt(9, event.getGroupId());
                } else {
                    ps.setNull(9, Types.INTEGER);
                }
                List<Integer> categoryIds = event.getCategoryIds();
                if (categoryIds != null && !categoryIds.isEmpty()) {
                    ps.setArray(10, conn.createArrayOf("integer", categoryIds.toArray()));
                } else {
                    ps.setNull(10, Types.ARRAY);
                }
                ps.setInt(11, event.getEventId());
                return ps.executeUpdate();
            });
            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            }
            return getEventById(event.getEventId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public int deleteEventById(int id) {
        String sql = "DELETE FROM events WHERE id = ?";
        try {
            return jdbcTemplate.update(sql, id);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }
}
