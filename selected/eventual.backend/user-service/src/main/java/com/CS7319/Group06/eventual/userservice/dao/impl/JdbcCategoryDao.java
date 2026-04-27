package com.CS7319.Group06.eventual.userservice.dao.impl;

import com.CS7319.Group06.eventual.userservice.dao.CategoryDao;
import com.CS7319.Group06.eventual.userservice.exception.DaoException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JDBC implementation of CategoryDao
 */
@Component
public class JdbcCategoryDao implements CategoryDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCategoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<String> getAllCategories() {
        String sql = "SELECT name FROM categories ORDER BY name";
        try {
            return jdbcTemplate.queryForList(sql, String.class);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }
}
