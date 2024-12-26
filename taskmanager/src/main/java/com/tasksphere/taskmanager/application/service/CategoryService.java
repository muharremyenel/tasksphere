package com.tasksphere.taskmanager.application.service;

import com.tasksphere.taskmanager.application.dto.category.CategoryResponse;
import com.tasksphere.taskmanager.application.dto.category.CreateCategoryRequest;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CreateCategoryRequest request);
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
    void deleteCategory(Long id);
} 