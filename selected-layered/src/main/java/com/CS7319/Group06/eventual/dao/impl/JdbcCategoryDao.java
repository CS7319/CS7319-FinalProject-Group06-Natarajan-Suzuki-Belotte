package com.CS7319.Group06.eventual.dao.impl;

import com.CS7319.Group06.eventual.dao.CategoryDao;
import com.CS7319.Group06.eventual.exception.DaoException;
import com.CS7319.Group06.eventual.model.Category;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JDBC implementation of CategoryDao
 *
 * @author harininatarajan
 */
@Component
public class JdbcCategoryDao implements CategoryDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCategoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Category> categoryRowMapper = (rs, rowNum) -> {
        Category category = new Category();
        category.setCategoryId(rs.getInt("id"));
        category.setName(rs.getString("name"));
        category.setType(rs.getString("type"));
        return category;
    };

    @Override
    public List<Category> getAllCategories() {
        String sql = "SELECT id, name, type FROM categories ORDER BY type, name";
        try {
            return jdbcTemplate.query(sql, categoryRowMapper);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
    }
}
