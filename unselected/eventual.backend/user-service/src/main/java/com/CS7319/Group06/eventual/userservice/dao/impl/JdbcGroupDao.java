package com.CS7319.Group06.eventual.userservice.dao.impl;

import com.CS7319.Group06.eventual.userservice.dao.GroupDao;
import com.CS7319.Group06.eventual.userservice.exception.DaoException;
import com.CS7319.Group06.eventual.userservice.model.Group;
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
 * JDBC implementation of GroupDao
 */
@Component
public class JdbcGroupDao implements GroupDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcGroupDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Group> groupRowMapper = (rs, rowNum) -> {
        Group group = new Group();
        group.setGroupId(rs.getInt("id"));
        group.setName(rs.getString("name"));
        group.setDescription(rs.getString("description"));
        group.setCreatorEmail(rs.getString("creator_email"));
        group.setOwnerEmail(rs.getString("owner_email"));
        group.setIsPublic(rs.getObject("is_public", Boolean.class));
        group.setModifiedBy(rs.getString("modified_by"));
        group.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        group.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        Array memberArray = rs.getArray("member_emails");
        if (memberArray != null) {
            String[] emails = (String[]) memberArray.getArray();
            group.setMemberEmails(new ArrayList<>(Arrays.asList(emails)));
        } else {
            group.setMemberEmails(new ArrayList<>());
        }

        return group;
    };

    @Override
    public Group getGroupById(int id) {
        String sql = "SELECT id, name, description, creator_email, owner_email, is_public, member_emails, modified_by, created_at, updated_at " +
                "FROM groups WHERE id = ?";
        try {
            List<Group> results = jdbcTemplate.query(sql, groupRowMapper, id);
            return results.isEmpty() ? null : results.getFirst();
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public Group createGroup(Group group) {
        String sql = "INSERT INTO groups (name, description, creator_email, owner_email, is_public, member_emails) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try {
            List<String> members = group.getMemberEmails();
            Integer newId = jdbcTemplate.execute((java.sql.Connection conn) -> {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, group.getName());
                ps.setString(2, group.getDescription());
                ps.setString(3, group.getCreatorEmail());
                ps.setString(4, group.getOwnerEmail());
                ps.setBoolean(5, Boolean.TRUE.equals(group.getIsPublic()));
                ps.setArray(6, conn.createArrayOf("text",
                        members != null ? members.toArray() : new String[0]));
                ps.execute();
                var rs = ps.getResultSet();
                rs.next();
                return rs.getInt(1);
            });
            return getGroupById(newId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("A group with that name already exists", e);
        }
    }

    @Override
    public Group updateGroup(Group group) {
        String sql = "UPDATE groups SET " +
                "name          = COALESCE(?, name), " +
                "description   = COALESCE(?, description), " +
                "owner_email   = COALESCE(?, owner_email), " +
                "is_public     = COALESCE(?, is_public), " +
                "member_emails = COALESCE(?, member_emails), " +
                "modified_by   = ?, " +
                "updated_at    = CURRENT_TIMESTAMP " +
                "WHERE id = ?";
        try {
            jdbcTemplate.execute((java.sql.Connection conn) -> {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, group.getName());
                ps.setString(2, group.getDescription());
                ps.setString(3, group.getOwnerEmail());
                if (group.getIsPublic() != null) {
                    ps.setBoolean(4, group.getIsPublic());
                } else {
                    ps.setNull(4, Types.BOOLEAN);
                }
                List<String> members = group.getMemberEmails();
                if (members != null) {
                    ps.setArray(5, conn.createArrayOf("text", members.toArray()));
                } else {
                    ps.setNull(5, Types.ARRAY);
                }
                ps.setString(6, group.getModifiedBy());
                ps.setInt(7, group.getGroupId());
                return ps.executeUpdate();
            });
            return getGroupById(group.getGroupId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("A group with that name already exists", e);
        }
    }

    @Override
    public Group addMember(int groupId, String memberEmail) {
        String sql = "UPDATE groups SET " +
                "member_emails = array_append(member_emails, ?), " +
                "updated_at    = CURRENT_TIMESTAMP " +
                "WHERE id = ? AND NOT (? = ANY(member_emails))";
        try {
            jdbcTemplate.update(sql, memberEmail, groupId, memberEmail);
            return getGroupById(groupId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public List<Group> getGroupsPaginated(int page, int size) {
        String sql = "SELECT id, name, description, creator_email, owner_email, is_public, member_emails, " +
                "modified_by, created_at, updated_at " +
                "FROM groups " +
                "ORDER BY id " +
                "LIMIT ? OFFSET ?";
        try {
            return jdbcTemplate.query(sql, groupRowMapper, size, (long) page * size);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public int countGroups() {
        String sql = "SELECT COUNT(*) FROM groups";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }
}
