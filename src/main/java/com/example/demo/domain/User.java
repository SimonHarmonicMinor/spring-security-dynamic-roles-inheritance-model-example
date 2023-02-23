package com.example.demo.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "users")
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;

    @OneToMany(fetch = LAZY, mappedBy = "user", cascade = PERSIST)
    private List<CommunityRole> communityRoles = new ArrayList<>();

    @OneToMany(fetch = LAZY, mappedBy = "user", cascade = PERSIST)
    private List<PostRole> postRoles = new ArrayList<>();

    public static User newUser(String name) {
        final var user = new User();
        user.name = name;
        return user;
    }

    public void addPostRole(Post post, PostRoleType postRoleType) {
        final var postRole = new PostRole();
        postRole.setPost(post);
        postRole.setType(postRoleType);
        postRole.setUser(this);
        postRoles.add(postRole);
    }

    public void addCommunityRole(Community community, CommunityRoleType communityRoleType) {
        final var communityRole = new CommunityRole();
        communityRole.setCommunity(community);
        communityRole.setType(communityRoleType);
        communityRole.setUser(this);
        communityRoles.add(communityRole);
    }
}
