package com.tasksphere.taskmanager.application.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final Set<String> blacklistedTokens = new HashSet<>();
    
    public void addToBlacklist(String token) {
        blacklistedTokens.add(token);
    }
    
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
} 