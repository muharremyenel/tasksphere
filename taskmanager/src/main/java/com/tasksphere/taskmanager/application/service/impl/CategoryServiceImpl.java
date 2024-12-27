package com.tasksphere.taskmanager.application.service.impl;

import com.tasksphere.taskmanager.application.dto.category.CategoryResponse;
import com.tasksphere.taskmanager.application.dto.category.CreateCategoryRequest;
import com.tasksphere.taskmanager.application.dto.category.UpdateCategoryRequest;
import com.tasksphere.taskmanager.application.service.CategoryService;
import com.tasksphere.taskmanager.domain.entity.Category;
import com.tasksphere.taskmanager.domain.entity.User;
import com.tasksphere.taskmanager.domain.exception.ResourceNotFoundException;
import com.tasksphere.taskmanager.domain.exception.UnauthorizedAccessException;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.CategoryRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        User currentUser = getCurrentUser();
        
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .colorHex(request.getColorHex())
                .creator(currentUser)
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return mapToCategoryResponse(category);
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
        category.setDescription(request.getDescription());
        
        Category updatedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(updatedCategory);
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .colorHex(category.getColorHex())
                .taskCount((long) category.getTasks().size())
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
} 