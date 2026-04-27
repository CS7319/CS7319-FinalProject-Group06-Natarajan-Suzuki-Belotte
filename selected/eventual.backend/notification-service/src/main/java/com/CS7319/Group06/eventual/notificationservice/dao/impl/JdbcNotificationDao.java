package com.CS7319.Group06.eventual.notificationservice.dao.impl;

import com.CS7319.Group06.eventual.notificationservice.dao.NotificationDao;
import com.CS7319.Group06.eventual.notificationservice.exception.DaoException;
import com.CS7319.Group06.eventual.notificationservice.model.Notification;
import com.CS7319.Group06.eventual.notificationservice.model.constants.NotificationReferenceType;
import com.CS7319.Group06.eventual.notificationservice.model.constants.NotificationType;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JDBC implementation of NotificationDao.
 */
@Component
public class JdbcNotificationDao implements NotificationDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNotificationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Notification> notificationRowMapper = (rs, rowNum) -> {
        Notification n = new Notification();
        n.setId(rs.getInt("id"));
        n.setRecipientEmail(rs.getString("recipient_email"));
        n.setType(NotificationType.valueOf(rs.getString("type")));
        n.setTitle(rs.getString("title"));
        n.setMessage(rs.getString("message"));
        n.setReferenceId(rs.getString("reference_id"));
        String refType = rs.getString("reference_type");
        if (refType != null) {
            n.setReferenceType(NotificationReferenceType.valueOf(refType));
        }
        n.setRead(rs.getBoolean("is_read"));
        n.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return n;
    };

    @Override
    public Notification createNotification(Notification notification) {
        String sql = """
                INSERT INTO notifications (recipient_email, type, title, message, reference_id, reference_type)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id
                """;
        try {
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class,
                    notification.getRecipientEmail(),
                    notification.getType().name(),
                    notification.getTitle(),
                    notification.getMessage(),
                    notification.getReferenceId(),
                    notification.getReferenceType() != null ? notification.getReferenceType().name() : null);
            return getById(id);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public List<Notification> getNotificationsForUser(String recipientEmail, int page, int size) {
        String sql = """
                SELECT id, recipient_email, type, title, message, reference_id, reference_type, is_read, created_at
                FROM notifications
                WHERE recipient_email = ?
                ORDER BY created_at DESC
                LIMIT ? OFFSET ?
                """;
        try {
            return jdbcTemplate.query(sql, notificationRowMapper, recipientEmail, size, (long) page * size);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public long countUnread(String recipientEmail) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE recipient_email = ? AND is_read = FALSE";
        try {
            Long count = jdbcTemplate.queryForObject(sql, Long.class, recipientEmail);
            return count != null ? count : 0L;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public Notification markAsRead(int id, String recipientEmail) {
        String sql = """
                UPDATE notifications SET is_read = TRUE
                WHERE id = ? AND recipient_email = ?
                """;
        try {
            int rows = jdbcTemplate.update(sql, id, recipientEmail);
            return rows > 0 ? getById(id) : null;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public int markAllAsRead(String recipientEmail) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE recipient_email = ? AND is_read = FALSE";
        try {
            return jdbcTemplate.update(sql, recipientEmail);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public int deleteById(int id, String recipientEmail) {
        String sql = "DELETE FROM notifications WHERE id = ? AND recipient_email = ?";
        try {
            return jdbcTemplate.update(sql, id, recipientEmail);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    private Notification getById(int id) {
        String sql = """
                SELECT id, recipient_email, type, title, message, reference_id, reference_type, is_read, created_at
                FROM notifications WHERE id = ?
                """;
        List<Notification> results = jdbcTemplate.query(sql, notificationRowMapper, id);
        return results.isEmpty() ? null : results.getFirst();
    }
}
