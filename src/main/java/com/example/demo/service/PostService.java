package com.example.demo.service;

import com.example.demo.controller.dto.PostResponse;
import com.example.demo.domain.Post;
import com.example.demo.repository.CommunityRepository;
import com.example.demo.repository.PostRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;

    @Transactional
    public PostResponse createPost(Long communityId, String name) {
        final var post = postRepository.save(Post.newPost(
            name,
            communityRepository.getReferenceById(communityId)
        ));
        return new PostResponse(post.getId(), post.getName());
    }

    @Transactional
    public void updatePost(Long postId, String name) {
        final var post = postRepository.findById(postId).orElseThrow();
        post.changeName(name);
    }

    @Transactional
    public PostResponse getPostById(Long postId) {
        return postRepository.findById(postId)
                   .map(post -> new PostResponse(post.getId(), post.getName()))
                   .orElseThrow();
    }
}
