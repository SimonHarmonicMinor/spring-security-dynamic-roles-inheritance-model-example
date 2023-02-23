package com.example.demo.service;

import com.example.demo.controller.dto.CommunityResponse;
import com.example.demo.domain.Community;
import com.example.demo.domain.CommunityRoleType;
import com.example.demo.domain.User;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.PlainAuthentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommunityResponse createCommunity(String name) {
        final var community = communityRepository.save(Community.newCommunity(name));
        final var currentUser = getCurrentUser();
        currentUser.addCommunityRole(community, CommunityRoleType.ADMIN);
        return new CommunityResponse(community.getId(), community.getName());
    }

    private User getCurrentUser() {
        final var userId = ((PlainAuthentication) SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        return userRepository.findById(userId).orElseThrow();
    }
}
