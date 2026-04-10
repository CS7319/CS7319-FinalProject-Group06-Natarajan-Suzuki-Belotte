package com.CS7319.Group06.eventual.service;

import com.CS7319.Group06.eventual.model.Category;

import java.util.List;

/**
 * Service to fetch all available categories
 *
 * @author harininatarajan
 */
public interface CategoryService {

    List<Category> getAllCategories();
}
