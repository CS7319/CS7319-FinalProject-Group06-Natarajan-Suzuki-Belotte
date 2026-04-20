package com.CS7319.Group06.eventual.userservice.service.impl;

import com.CS7319.Group06.eventual.userservice.dao.CategoryDao;
import com.CS7319.Group06.eventual.userservice.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation for CategoryService
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public List<String> getAllCategories() {
        return categoryDao.getAllCategories();
    }
}
