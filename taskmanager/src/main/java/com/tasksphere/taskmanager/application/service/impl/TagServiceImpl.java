package com.tasksphere.taskmanager.application.service.impl;

import com.tasksphere.taskmanager.application.dto.tag.CreateTagRequest;
import com.tasksphere.taskmanager.application.dto.tag.TagResponse;
import com.tasksphere.taskmanager.application.service.TagService;
import com.tasksphere.taskmanager.domain.entity.Tag;
import com.tasksphere.taskmanager.domain.entity.User;
import com.tasksphere.taskmanager.domain.exception.ResourceNotFoundException;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.TagRepository;
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
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Override
    public TagResponse createTag(CreateTagRequest request) {
        User currentUser = getCurrentUser();

        Tag tag = Tag.builder()
                .name(request.getName())
                .colorHex(request.getColorHex())
                .creator(currentUser)
                .build();

        Tag savedTag = tagRepository.save(tag);
        return mapToTagResponse(savedTag);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::mapToTagResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TagResponse getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        return mapToTagResponse(tag);
    }

    @Override
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tag not found");
        }
        tagRepository.deleteById(id);
    }

    private TagResponse mapToTagResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .colorHex(tag.getColorHex())
                .usageCount((long) tag.getTasks().size())
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
} 