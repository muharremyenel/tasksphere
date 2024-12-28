package com.tasksphere.taskmanager.application.service.impl;

import com.tasksphere.taskmanager.application.dto.category.CategoryResponse;
import com.tasksphere.taskmanager.application.dto.category.CreateCategoryRequest;
import com.tasksphere.taskmanager.application.dto.category.UpdateCategoryRequest;
import com.tasksphere.taskmanager.application.service.CategoryService;
import com.tasksphere.taskmanager.domain.entity.Category;
import com.tasksphere.taskmanager.domain.exception.ResourceNotFoundException;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
            .map(this::mapToCategoryResponse)
            .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = Category.builder()
            .name(request.getName())
            .colorHex(request.getColorHex())
            .build();

        return mapToCategoryResponse(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setName(request.getName());
        category.setColorHex(request.getColorHex());

        return mapToCategoryResponse(categoryRepository.save(category));
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
            .id(category.getId())
            .name(category.getName())
            .colorHex(category.getColorHex())
            .build();
    }
} 