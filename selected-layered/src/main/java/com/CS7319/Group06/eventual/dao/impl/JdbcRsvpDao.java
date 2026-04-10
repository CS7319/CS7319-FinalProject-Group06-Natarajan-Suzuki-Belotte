package com.CS7319.Group06.eventual.dao.impl;

import com.CS7319.Group06.eventual.dao.RsvpDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.Rsvp;
import com.CS7319.Group06.eventual.model.constants.RsvpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JDBC implementation of RsvpDao
 *
 * @author harininatarajan
 */
@Component
public class JdbcRsvpDao implements RsvpDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcRsvpDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Rsvp> rsvpRowMapper = (rs, rowNum) -> {
        Rsvp rsvp = new Rsvp();
        rsvp.setId(rs.getInt("id"));
        rsvp.setEventId(rs.getInt("event_id"));
        rsvp.setUserEmail(rs.getString("user_email"));
        rsvp.setStatus(RsvpStatus.valueOf(rs.getString("status")));
        rsvp.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        rsvp.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return rsvp;
    };

    @Override
    public Rsvp getRsvpByEventAndUser(int eventId, String userEmail) {
        String sql = "SELECT id, event_id, user_email, status, created_at, updated_at " +
                     "FROM rsvp WHERE event_id = ? AND user_email = ?";
        try {
            List<Rsvp> results = jdbcTemplate.query(sql, rsvpRowMapper, eventId, userEmail);
            return results.isEmpty() ? null : results.getFirst();
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public List<Rsvp> getRsvpsByEvent(int eventId) {
        String sql = "SELECT id, event_id, user_email, status, created_at, updated_at " +
                     "FROM rsvp WHERE event_id = ? ORDER BY created_at ASC";
        try {
            return jdbcTemplate.query(sql, rsvpRowMapper, eventId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public Rsvp createRsvp(Rsvp rsvp) {
        String sql = "INSERT INTO rsvp (event_id, user_email, status) VALUES (?, ?, ?) RETURNING id";
        try {
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class,
                    rsvp.getEventId(), rsvp.getUserEmail(), rsvp.getStatus().name());
            if (id == null) {
                throw new DaoException("Insert returned no id");
            }
            return getRsvpByEventAndUser(rsvp.getEventId(), rsvp.getUserEmail());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("RSVP already exists for this user and event", e);
        }
    }

    @Override
    public Rsvp updateRsvpStatus(int eventId, String userEmail, RsvpStatus status) {
        String sql = "UPDATE rsvp SET status = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE event_id = ? AND user_email = ?";
        try {
            jdbcTemplate.update(sql, status.name(), eventId, userEmail);
            return getRsvpByEventAndUser(eventId, userEmail);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public int countGoingByEvent(int eventId) {
        String sql = "SELECT COUNT(*) FROM rsvp WHERE event_id = ? AND status = 'GOING'";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, eventId);
            return count != null ? count : 0;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public Rsvp getFirstWaitlisted(int eventId) {
        // Ordered by created_at so the earliest waitlisted gets promoted first
        String sql = "SELECT id, event_id, user_email, status, created_at, updated_at " +
                     "FROM rsvp WHERE event_id = ? AND status = 'WAITLISTED' " +
                     "ORDER BY created_at ASC LIMIT 1";
        try {
            List<Rsvp> results = jdbcTemplate.query(sql, rsvpRowMapper, eventId);
            return results.isEmpty() ? null : results.getFirst();
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }
}
