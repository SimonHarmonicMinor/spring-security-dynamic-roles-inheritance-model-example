package com.example.demo.domain;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

public enum PostRoleType implements Role {
    VIEWER, EDITOR, REPORTER;
    private final Set<Role> children = new HashSet<>();

    static {
        REPORTER.children.add(VIEWER);
        EDITOR.children.add(VIEWER);
    }

    @Override
    public boolean includes(Role role) {
        return this.equals(role) || children.stream().anyMatch(r -> r.includes(role));
    }

    @Component("PostRole")
    @Getter
    static class SpringComponent {
        private final PostRoleType VIEWER = PostRoleType.VIEWER;
        private final PostRoleType EDITOR = PostRoleType.EDITOR;
        private final PostRoleType REPORTER = PostRoleType.REPORTER;
    }
}
