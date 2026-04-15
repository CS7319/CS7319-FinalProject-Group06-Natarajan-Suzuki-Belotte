package com.CS7319.Group06.eventual.dao;

import java.util.List;

/**
 * Data layer for categories
 *
 * @author harininatarajan
 */
public interface CategoryDao {

    /**
     * To retrieve all the categories
     *
     * @return
     */
    List<String> getAllCategoryTypes();
}
