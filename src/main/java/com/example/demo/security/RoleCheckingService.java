package com.example.demo.security;

import com.example.demo.domain.Role;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("RoleCheckingService")
public class RoleCheckingService {
    public boolean hasAnyRoleByCommunityId(Authentication authentication, Long communityId, Role... roles) {

    }

    public boolean hasAnyRoleByPostId(Authentication authentication, Long postId, Role... roles) {

    }
}
