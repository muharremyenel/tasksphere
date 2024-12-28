package com.tasksphere.taskmanager.application.service.impl;

import com.tasksphere.taskmanager.application.dto.team.CreateTeamRequest;
import com.tasksphere.taskmanager.application.dto.team.TeamResponse;
import com.tasksphere.taskmanager.application.dto.team.UpdateTeamRequest;
import com.tasksphere.taskmanager.application.dto.user.UserSummary;
import com.tasksphere.taskmanager.application.service.TeamService;
import com.tasksphere.taskmanager.domain.entity.Team;
import com.tasksphere.taskmanager.domain.entity.User;
import com.tasksphere.taskmanager.domain.exception.ResourceNotFoundException;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.TeamRepository;
import com.tasksphere.taskmanager.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    public List<TeamResponse> getTeams() {
        return teamRepository.findAll().stream()
            .map(this::mapToTeamResponse)
            .collect(Collectors.toList());
    }

    @Override
    public TeamResponse createTeam(CreateTeamRequest request) {
        User teamLead = request.getTeamLeadId() != null ?
            userRepository.findById(request.getTeamLeadId())
                .orElseThrow(() -> new ResourceNotFoundException("Team lead not found")) : null;

        Team team = Team.builder()
            .name(request.getName())
            .description(request.getDescription())
            .teamLead(teamLead)
            .build();

        Team savedTeam = teamRepository.save(team);
        return mapToTeamResponse(savedTeam);
    }

    @Override
    public void addMember(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setTeam(team);
        userRepository.save(user);
    }

    @Override
    public void removeMember(Long teamId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getTeam() != null && user.getTeam().getId().equals(teamId)) {
            user.setTeam(null);
            userRepository.save(user);
        }
    }

    @Override
    public TeamResponse getMyTeam() {
        User currentUser = getCurrentUser();
        return currentUser.getTeam() != null ? mapToTeamResponse(currentUser.getTeam()) : null;
    }

    @Override
    public List<UserSummary> getTeamMembers(Long teamId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        return team.getMembers().stream()
            .map(this::mapToUserSummary)
            .collect(Collectors.toList());
    }

    @Override
    public boolean isMemberOfTeam(Long teamId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getTeam() != null && user.getTeam().getId().equals(teamId);
    }

    @Override
    public TeamResponse updateTeam(Long id, UpdateTeamRequest request) {
        Team team = teamRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        team.setName(request.getName());
        team.setDescription(request.getDescription());

        if (request.getTeamLeadId() != null) {
            User teamLead = userRepository.findById(request.getTeamLeadId())
                .orElseThrow(() -> new ResourceNotFoundException("Team lead not found"));
            team.setTeamLead(teamLead);
        }

        return mapToTeamResponse(teamRepository.save(team));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private TeamResponse mapToTeamResponse(Team team) {
        return TeamResponse.builder()
            .id(team.getId())
            .name(team.getName())
            .description(team.getDescription())
            .teamLead(team.getTeamLead() != null ? mapToUserSummary(team.getTeamLead()) : null)
            .members(team.getMembers().stream()
                .map(this::mapToUserSummary)
                .collect(Collectors.toSet()))
            .createdAt(team.getCreatedAt())
            .updatedAt(team.getUpdatedAt())
            .build();
    }

    private UserSummary mapToUserSummary(User user) {
        return UserSummary.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }
} 