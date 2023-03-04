package com.vuntfx17196.service;

import com.vuntfx17196.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategory();

    Category getCategoryById(int id);

    long countAllCategories();

    void updateCategory(Category category);

    void deleteCategoryById(int id);
}
