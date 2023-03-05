package com.example.demo.controller;

import com.example.demo.controller.dto.CommunityResponse;
import com.example.demo.controller.dto.PostResponse;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

class ControllerTest extends AbstractControllerTest {
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        transactionTemplate.executeWithoutResult(
            status -> DatabaseUtil.cleanDatabase(jdbcTemplate)
        );
    }

    @Test
    void shouldReturn401IfUnauthorizedUserTryingToCreateCommunity() {
        userRepository.save(User.newUser("john"));

        final var communityCreatedResponse =
            rest.postForEntity(
                "/api/community?name={name}",
                null,
                CommunityResponse.class,
                Map.of("name", "community_name")
            );

        assertEquals(UNAUTHORIZED, communityCreatedResponse.getStatusCode());
    }

    @Test
    void shouldCreateCommunityAndPostSuccessfully() {
        userRepository.save(User.newUser("john"));

        final var communityCreatedResponse =
            rest.withBasicAuth("john", "password")
                .postForEntity(
                    "/api/community?name={name}",
                    null,
                    CommunityResponse.class,
                    Map.of("name", "community_name")
                );
        assertTrue(communityCreatedResponse.getStatusCode().is2xxSuccessful());
        final var communityId = communityCreatedResponse.getBody().id();

        final var postCreatedResponse =
            rest.withBasicAuth("john", "password")
                .postForEntity(
                    "/api/community/{communityId}/post?name={name}",
                    null,
                    PostResponse.class,
                    Map.of("communityId", communityId, "name", "post_name")
                );
        assertTrue(postCreatedResponse.getStatusCode().is2xxSuccessful());
    }

    @Test
    void shouldReturn403IfUserHasNoAccessToViewThePost() {
        userRepository.save(User.newUser("john"));
        userRepository.save(User.newUser("bob"));

        final var communityCreatedResponse =
            rest.withBasicAuth("john", "password")
                .postForEntity(
                    "/api/community?name={name}",
                    null,
                    CommunityResponse.class,
                    Map.of("name", "community_name")
                );
        assertTrue(communityCreatedResponse.getStatusCode().is2xxSuccessful());
        final var communityId = communityCreatedResponse.getBody().id();

        final var postCreatedResponse =
            rest.withBasicAuth("john", "password")
                .postForEntity(
                    "/api/community/{communityId}/post?name={name}",
                    null,
                    PostResponse.class,
                    Map.of("communityId", communityId, "name", "post_name")
                );
        assertTrue(postCreatedResponse.getStatusCode().is2xxSuccessful());
        final var postId = postCreatedResponse.getBody().id();

        final var postViewResponse =
            rest.withBasicAuth("bob", "password")
                .getForEntity(
                    "/api/post/{postId}",
                    PostResponse.class,
                    Map.of("postId", postId)
                );
        assertEquals(FORBIDDEN, postViewResponse.getStatusCode());
    }
}