package com.CS7319.Group06.eventual.dao;

import com.CS7319.Group06.eventual.model.Category;

import java.util.List;

/**
 * Data layer for categories
 *
 * @author harininatarajan
 */
public interface CategoryDao {

    List<Category> getAllCategories();
}
