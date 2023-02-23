package com.example.demo.controller;

import com.example.demo.controller.dto.CommunityResponse;
import com.example.demo.controller.dto.PostResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Controller {
    @PostMapping("/community")
    @PreAuthorize("isAuthenticated()")
    public CommunityResponse createCommunity(@RequestParam String name) {

    }

    @PostMapping("/community/{communityId}/post")
    @PreAuthorize("@RoleService.hasAnyRoleByCommunityId(authentication, #communityId, @CommunityRole.ADMIN)")
    public PostResponse createPost(@PathVariable Long communityId, @RequestParam String name) {

    }

    @PutMapping("/post/{postId}")
    @PreAuthorize("@RoleService.hasAnyRoleByPostId(authentication, #postId, @PostRole.EDITOR)")
    public void updatePost(@PathVariable Long postId, @RequestParam String name) {

    }

    @GetMapping("/post/{postId}")
    @PreAuthorize("@RoleService.hasAnyRoleByPostId(authentication, #postId, @PostRole.VIEWER)")
    public PostResponse getPost(@PathVariable Long postId) {

    }
}
