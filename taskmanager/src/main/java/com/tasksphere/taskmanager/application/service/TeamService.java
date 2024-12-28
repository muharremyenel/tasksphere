package com.tasksphere.taskmanager.application.service;

import com.tasksphere.taskmanager.application.dto.team.CreateTeamRequest;
import com.tasksphere.taskmanager.application.dto.team.TeamResponse;
import com.tasksphere.taskmanager.application.dto.team.UpdateTeamRequest;
import com.tasksphere.taskmanager.application.dto.user.UserSummary;
import java.util.List;

public interface TeamService {
    List<TeamResponse> getTeams();
    TeamResponse createTeam(CreateTeamRequest request);
    void addMember(Long teamId, Long userId);
    void removeMember(Long teamId, Long userId);
    TeamResponse getMyTeam();
    List<UserSummary> getTeamMembers(Long teamId);
    boolean isMemberOfTeam(Long teamId, Long userId);
    TeamResponse updateTeam(Long id, UpdateTeamRequest request);
} 