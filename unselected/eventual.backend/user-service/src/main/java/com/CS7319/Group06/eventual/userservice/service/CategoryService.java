package com.CS7319.Group06.eventual.userservice.service;

import java.util.List;

/**
 * Service to fetch all available category types
 */
public interface CategoryService {

    /**
     * List all the categories
     *
     * @return
     */
    List<String> getAllCategories();
}
