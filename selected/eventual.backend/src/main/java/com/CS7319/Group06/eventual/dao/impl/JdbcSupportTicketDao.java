package com.CS7319.Group06.eventual.dao.impl;

import com.CS7319.Group06.eventual.dao.SupportTicketDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.SupportTicket;
import com.CS7319.Group06.eventual.model.constants.TicketStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

/**
 * JDBC implementation of SupportTicketDao.
 */
@Component
public class JdbcSupportTicketDao implements SupportTicketDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSupportTicketDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<SupportTicket> ticketRowMapper = (rs, rowNum) -> {
        SupportTicket t = new SupportTicket();
        t.setTicketId(rs.getInt("id"));
        t.setSubject(rs.getString("subject"));
        t.setContent(rs.getString("content"));
        t.setSubmittedBy(rs.getString("submitted_by"));
        t.setStatus(TicketStatus.valueOf(rs.getString("status")));
        Timestamp resolvedAt = rs.getTimestamp("resolved_at");
        t.setResolvedAt(resolvedAt != null ? resolvedAt.toLocalDateTime() : null);
        t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        t.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return t;
    };

    @Override
    public SupportTicket createTicket(SupportTicket ticket) {
        String sql = "INSERT INTO support_tickets (subject, content, submitted_by) " +
                "VALUES (?, ?, ?) RETURNING id";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, ticket.getSubject());
                ps.setString(2, ticket.getContent());
                ps.setString(3, ticket.getSubmittedBy());
                return ps;
            }, keyHolder);
            int id = ((Number) keyHolder.getKeys().get("id")).intValue();
            return getTicketById(id);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public SupportTicket getTicketById(int id) {
        String sql = "SELECT * FROM support_tickets WHERE id = ?";
        try {
            List<SupportTicket> results = jdbcTemplate.query(sql, ticketRowMapper, id);
            return results.isEmpty() ? null : results.getFirst();
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public List<SupportTicket> getTicketsByUser(String email) {
        String sql = "SELECT * FROM support_tickets WHERE submitted_by = ? ORDER BY created_at DESC";
        try {
            return jdbcTemplate.query(sql, ticketRowMapper, email);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public List<SupportTicket> getAllTickets(String status) {
        try {
            if (status != null) {
                String sql = "SELECT * FROM support_tickets WHERE status = ? ORDER BY created_at DESC";
                return jdbcTemplate.query(sql, ticketRowMapper, status.toUpperCase());
            }
            String sql = "SELECT * FROM support_tickets ORDER BY created_at DESC";
            return jdbcTemplate.query(sql, ticketRowMapper);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public SupportTicket updateStatus(int id, String status) {
        String sql = "UPDATE support_tickets SET " +
                "status     = ?, " +
                "resolved_at = CASE WHEN ? = 'RESOLVED' THEN CURRENT_TIMESTAMP ELSE NULL END, " +
                "updated_at  = CURRENT_TIMESTAMP " +
                "WHERE id = ?";
        try {
            int rows = jdbcTemplate.update(sql, status.toUpperCase(), status.toUpperCase(), id);
            if (rows == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            }
            return getTicketById(id);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }
}
