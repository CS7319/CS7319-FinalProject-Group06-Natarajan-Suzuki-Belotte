package com.CS7319.Group06.eventual.userservice.dao.impl;

import com.CS7319.Group06.eventual.userservice.dao.GroupJoinRequestDao;
import com.CS7319.Group06.eventual.userservice.exception.DaoException;
import com.CS7319.Group06.eventual.userservice.model.GroupJoinRequest;
import com.CS7319.Group06.eventual.userservice.model.constants.JoinRequestStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JDBC implementation of GroupJoinRequestDao
 */
@Component
public class JdbcGroupJoinRequestDao implements GroupJoinRequestDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcGroupJoinRequestDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<GroupJoinRequest> rowMapper = (rs, rowNum) -> {
        GroupJoinRequest req = new GroupJoinRequest();
        req.setId(rs.getInt("id"));
        req.setGroupId(rs.getInt("group_id"));
        req.setRequesterEmail(rs.getString("requester_email"));
        req.setStatus(JoinRequestStatus.valueOf(rs.getString("status")));
        req.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        req.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return req;
    };

    @Override
    public GroupJoinRequest getRequestById(int id) {
        String sql = "SELECT id, group_id, requester_email, status, created_at, updated_at " +
                     "FROM group_join_requests WHERE id = ?";
        try {
            List<GroupJoinRequest> results = jdbcTemplate.query(sql, rowMapper, id);
            return results.isEmpty() ? null : results.getFirst();
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public GroupJoinRequest getRequestByGroupAndUser(int groupId, String requesterEmail) {
        String sql = "SELECT id, group_id, requester_email, status, created_at, updated_at " +
                     "FROM group_join_requests WHERE group_id = ? AND requester_email = ?";
        try {
            List<GroupJoinRequest> results = jdbcTemplate.query(sql, rowMapper, groupId, requesterEmail);
            return results.isEmpty() ? null : results.getFirst();
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public List<GroupJoinRequest> getPendingRequestsByGroup(int groupId) {
        String sql = "SELECT id, group_id, requester_email, status, created_at, updated_at " +
                     "FROM group_join_requests WHERE group_id = ? AND status = 'PENDING' " +
                     "ORDER BY created_at ASC";
        try {
            return jdbcTemplate.query(sql, rowMapper, groupId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public GroupJoinRequest createRequest(GroupJoinRequest request) {
        String sql = "INSERT INTO group_join_requests (group_id, requester_email, status) " +
                     "VALUES (?, ?, ?) RETURNING id";
        try {
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class,
                    request.getGroupId(), request.getRequesterEmail(), request.getStatus().name());
            if (id == null) {
                throw new DaoException("Insert returned no id");
            }
            return getRequestById(id);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("A join request already exists for this user and group", e);
        }
    }

    @Override
    public GroupJoinRequest updateRequestStatus(int id, JoinRequestStatus status) {
        String sql = "UPDATE group_join_requests SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try {
            jdbcTemplate.update(sql, status.name(), id);
            return getRequestById(id);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }
}
