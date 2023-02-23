package com.example.demo.security;

import com.example.demo.domain.CommunityRoleType;
import com.example.demo.domain.PostRoleType;
import com.example.demo.domain.Role;
import com.example.demo.repository.CommunityRoleRepository;
import com.example.demo.repository.PostRoleRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Set;

import lombok.RequiredArgsConstructor;

@Service("RoleService")
@RequiredArgsConstructor
public class RoleService {
    private final CommunityRoleRepository communityRoleRepository;
    private final PostRoleRepository postRoleRepository;

    public boolean hasAnyRoleByCommunityId(Authentication authentication, Long communityId, Role... roles) {
        final Long userId = ((PlainAuthentication) authentication).getPrincipal();
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

    public boolean hasAnyRoleByPostId(Authentication authentication, Long postId, Role... roles) {

    }
}
