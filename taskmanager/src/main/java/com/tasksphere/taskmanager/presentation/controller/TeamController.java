package com.tasksphere.taskmanager.presentation.controller;

import com.tasksphere.taskmanager.application.dto.team.CreateTeamRequest;
import com.tasksphere.taskmanager.application.dto.team.TeamResponse;
import com.tasksphere.taskmanager.application.dto.team.UpdateTeamRequest;
import com.tasksphere.taskmanager.application.dto.user.UserSummary;
import com.tasksphere.taskmanager.application.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getTeams() {
        return ResponseEntity.ok(teamService.getTeams());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamResponse> createTeam(@RequestBody CreateTeamRequest request) {
        return ResponseEntity.ok(teamService.createTeam(request));
    }

    @PutMapping("/{teamId}/members/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addMember(@PathVariable Long teamId, @PathVariable Long userId) {
        teamService.addMember(teamId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-team")
    public ResponseEntity<TeamResponse> getMyTeam() {
        return ResponseEntity.ok(teamService.getMyTeam());
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<UserSummary>> getTeamMembers(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.getTeamMembers(teamId));
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeMember(@PathVariable Long teamId, @PathVariable Long userId) {
        teamService.removeMember(teamId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamResponse> updateTeam(
        @PathVariable Long id,
        @Valid @RequestBody UpdateTeamRequest request
    ) {
        return ResponseEntity.ok(teamService.updateTeam(id, request));
    }
} 