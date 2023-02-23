package com.example.demo.security;

import com.example.demo.domain.CommunityRoleType;
import com.example.demo.domain.PostRoleType;
import com.example.demo.domain.Role;
import com.example.demo.repository.CommunityRoleRepository;
import com.example.demo.repository.PostRoleRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import lombok.RequiredArgsConstructor;

@Service("RoleService")
@RequiredArgsConstructor
public class RoleService {
    private final CommunityRoleRepository communityRoleRepository;
    private final PostRoleRepository postRoleRepository;

    @Transactional
    public boolean hasAnyRoleByCommunityId(Long communityId, Role... roles) {
        final Long userId = getCurrentAuthentication().getPrincipal();
        final Set<CommunityRoleType> communityRoleTypes =
            communityRoleRepository.findRoleTypesByUserIdAndCommunityId(userId, communityId);
        for (Role role : roles) {
            if (communityRoleTypes.stream().anyMatch(communityRoleType -> communityRoleType.includes(role))) {
                return true;
            }
        }
        final Set<PostRoleType> postRoleTypes =
            postRoleRepository.findRoleTypesByUserIdAndCommunityId(userId, communityId);
        for (Role role : roles) {
            if (postRoleTypes.stream().anyMatch(postRoleType -> postRoleType.includes(role))) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean hasAnyRoleByPostId(Long postId, Role... roles) {
        final Long userId = getCurrentAuthentication().getPrincipal();
        final Set<CommunityRoleType> communityRoleTypes =
            communityRoleRepository.findRoleTypesByUserIdAndPostId(userId, postId);
        for (Role role : roles) {
            if (communityRoleTypes.stream().anyMatch(communityRoleType -> communityRoleType.includes(role))) {
                return true;
            }
        }
        final Set<PostRoleType> postRoleTypes =
            postRoleRepository.findRoleTypesByUserIdAndPostId(userId, postId);
        for (Role role : roles) {
            if (postRoleTypes.stream().anyMatch(postRoleType -> postRoleType.includes(role))) {
                return true;
            }
        }
        return false;
    }

    private static PlainAuthentication getCurrentAuthentication() {
        return (PlainAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
