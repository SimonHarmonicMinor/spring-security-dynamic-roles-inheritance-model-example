package com.example.demo.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum CommunityRoleType implements Role {
    ADMIN, MODERATOR;

    private final Set<Role> children = new HashSet<>();

    static {
        ADMIN.children.add(MODERATOR);
        MODERATOR.children.addAll(List.of(PostRoleType.EDITOR, PostRoleType.REPORTER));
    }

    @Override
    public boolean includes(Role role) {
        return this.equals(role) || children.stream().anyMatch(r -> r.includes(role));
    }
}
