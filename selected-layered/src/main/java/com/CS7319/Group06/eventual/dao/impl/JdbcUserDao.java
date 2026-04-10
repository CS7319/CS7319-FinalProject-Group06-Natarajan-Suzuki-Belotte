package com.CS7319.Group06.eventual.dao.impl;

import com.CS7319.Group06.eventual.dao.UserDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.User;
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
 * JDBC implementation of UserDao
 *
 * @author harininatarajan
 */
@Component
public class JdbcUserDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setEmail(rs.getString("email"));
        user.setName(rs.getString("name"));
        user.setPronoun(rs.getString("pronoun"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setProfilePicturePath(rs.getString("profile_picture"));
        user.setLocation(rs.getString("location"));
        user.setAboutMe(rs.getString("about_me"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        Array categoryArray = rs.getArray("category_types");
        if (categoryArray != null) {
            String[] types = (String[]) categoryArray.getArray();
            user.setCategoryTypes(new ArrayList<>(Arrays.asList(types)));
        } else {
            user.setCategoryTypes(new ArrayList<>());
        }

        Array groupArray = rs.getArray("group_ids");
        if (groupArray != null) {
            Integer[] ids = (Integer[]) groupArray.getArray();
            user.setGroupIds(new ArrayList<>(Arrays.asList(ids)));
        } else {
            user.setGroupIds(new ArrayList<>());
        }

        return user;
    };

    @Override
    public User createProfile(User user) {
        String sql = "INSERT INTO users (email, name, pronoun, password_hash, role, profile_picture, location, about_me, category_types, group_ids) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            List<String> categoryTypes = user.getCategoryTypes();
            List<Integer> groupIds = user.getGroupIds();
            jdbcTemplate.execute((java.sql.Connection conn) -> {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getName());
                ps.setString(3, user.getPronoun());
                ps.setString(4, user.getPasswordHash());
                ps.setString(5, user.getRole());
                ps.setString(6, user.getProfilePicturePath());
                ps.setString(7, user.getLocation());
                ps.setString(8, user.getAboutMe());
                ps.setArray(9, conn.createArrayOf("text",
                        categoryTypes != null ? categoryTypes.toArray() : new String[0]));
                ps.setArray(10, conn.createArrayOf("integer",
                        groupIds != null ? groupIds.toArray() : new Integer[0]));
                return ps.executeUpdate();
            });
            return getUserByEmail(user.getEmail());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Email already registered", e);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT email, name, pronoun, password_hash, role, profile_picture, " +
                "location, about_me, category_types, group_ids, created_at FROM users WHERE email = ?";
        try {
            List<User> results = jdbcTemplate.query(sql, userRowMapper, email);
            return results.isEmpty() ? null : results.getFirst();
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }

    @Override
    public void updateProfile(User user) {
        String sql = "UPDATE users SET " +
                "pronoun         = COALESCE(?, pronoun), " +
                "location        = COALESCE(?, location), " +
                "about_me        = COALESCE(?, about_me), " +
                "role            = COALESCE(?, role), " +
                "password_hash   = COALESCE(?, password_hash), " +
                "profile_picture = COALESCE(?, profile_picture), " +
                "category_types  = COALESCE(?, category_types), " +
                "group_ids       = COALESCE(?, group_ids) " +
                "WHERE email = ?";
        try {
            jdbcTemplate.execute((java.sql.Connection conn) -> {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, user.getPronoun());
                ps.setString(2, user.getLocation());
                ps.setString(3, user.getAboutMe());
                ps.setString(4, user.getRole());
                ps.setString(5, user.getPasswordHash());
                ps.setString(6, user.getProfilePicturePath());
                List<String> categoryTypes = user.getCategoryTypes();
                if (categoryTypes != null && !categoryTypes.isEmpty()) {
                    ps.setArray(7, conn.createArrayOf("text", categoryTypes.toArray()));
                } else {
                    ps.setNull(7, Types.ARRAY);
                }
                List<Integer> groupIds = user.getGroupIds();
                if (groupIds != null && !groupIds.isEmpty()) {
                    ps.setArray(8, conn.createArrayOf("integer", groupIds.toArray()));
                } else {
                    ps.setNull(8, Types.ARRAY);
                }
                ps.setString(9, user.getEmail());
                return ps.executeUpdate();
            });
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }
}
