package com.example.demo.controller;

import com.example.demo.controller.dto.CommunityResponse;
import com.example.demo.controller.dto.PostResponse;
import com.example.demo.service.CommunityService;
import com.example.demo.service.PostService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Controller {
    private final CommunityService communityService;
    private final PostService postService;

    @PostMapping("/community")
    @PreAuthorize("isAuthenticated()")
    public CommunityResponse createCommunity(@RequestParam String name) {
        return communityService.createCommunity(name);
    }

    @PostMapping("/community/{communityId}/post")
    @PreAuthorize("@RoleService.hasAnyRoleByCommunityId(authentication, #communityId, @CommunityRole.ADMIN)")
    public PostResponse createPost(@PathVariable Long communityId, @RequestParam String name) {
        return postService.createPost(communityId, name);
    }

    @PutMapping("/post/{postId}")
    @PreAuthorize("@RoleService.hasAnyRoleByPostId(authentication, #postId, @PostRole.EDITOR)")
    public void updatePost(@PathVariable Long postId, @RequestParam String name) {
        postService.updatePost(postId, name);
    }

    @GetMapping("/post/{postId}")
    @PreAuthorize("@RoleService.hasAnyRoleByPostId(authentication, #postId, @PostRole.VIEWER)")
    public PostResponse getPost(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }
}
