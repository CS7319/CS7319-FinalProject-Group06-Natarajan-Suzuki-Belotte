package com.CS7319.Group06.eventual.service;

import java.util.List;

/**
 * Service to fetch all available category types
 *
 * @author harininatarajan
 */
public interface CategoryService {

    /**
     * List all the categories
     *
     * @return
     */
    List<String> getAllCategoryTypes();
}
