package com.example.demo.repository;

import com.example.demo.domain.PostRole;
import com.example.demo.domain.PostRoleType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface PostRoleRepository extends JpaRepository<PostRole, Long> {
    @Query("""
        SELECT pr.type FROM PostRole pr
        JOIN pr.post p
        JOIN p.community c
        WHERE pr.user.id = :userId AND c.id = :communityId
        """)
    Set<PostRoleType> findRoleTypesByUserIdAndCommunityId(Long userId, Long communityId);

    @Query("""
        SELECT pr.type FROM PostRole pr
        WHERE pr.user.id = :userId AND pr.post.id = :postId
        """)
    Set<PostRoleType> findRoleTypesByUserIdAndPostId(Long userId, Long postId);
}
