package com.example.demo.domain;

import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RoleTest {
    @Test
    void shouldNotThrowStackOverflowException() {
        final var roots = Role.roots();
        final var existingRoles = Stream.concat(
            stream(PostRoleType.values()),
            stream(CommunityRoleType.values())
        ).toList();

        assertDoesNotThrow(
            () -> {
                for (Role root : roots) {
                    for (var roleToCheck : existingRoles) {
                        root.includes(roleToCheck);
                    }
                }
            }
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgs")
    void shouldIncludeOrNotTheGivenRoles(Role root, Set<Role> rolesToCheck, boolean shouldInclude) {
        for (Role role : rolesToCheck) {
            assertEquals(
                shouldInclude,
                root.includes(role)
            );
        }
    }

    private static Stream<Arguments> provideArgs() {
        return Stream.of(
            arguments(
                CommunityRoleType.ADMIN,
                Stream.concat(
                    stream(PostRoleType.values()),
                    stream(CommunityRoleType.values())
                ).collect(Collectors.toSet()),
                true
            ),
            arguments(
                CommunityRoleType.MODERATOR,
                Set.of(PostRoleType.EDITOR, PostRoleType.VIEWER, PostRoleType.REPORTER, CommunityRoleType.MODERATOR),
                true
            ),
            arguments(
                PostRoleType.VIEWER,
                Set.of(PostRoleType.REPORTER),
                false
            ),
            arguments(
                CommunityRoleType.MODERATOR,
                Set.of(CommunityRoleType.ADMIN),
                false
            )
        );
    }
}