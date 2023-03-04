package com.vuntfx17196.service.impl;

import com.vuntfx17196.model.Category;
import com.vuntfx17196.repository.CategoryRepository;
import com.vuntfx17196.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(int id) {
        return categoryRepository.findById(id);
    }

    @Override
    public long countAllCategories() {
        return categoryRepository.count();
    }

    @Override
    public void updateCategory(Category category) {
        categoryRepository.save(category);

    } // add or update tuy vao pri-key

    @Override
    public void deleteCategoryById(int id) {
        categoryRepository.deleteById(id);
    }

}
