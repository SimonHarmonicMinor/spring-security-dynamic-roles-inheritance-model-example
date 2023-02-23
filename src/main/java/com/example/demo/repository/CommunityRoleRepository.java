package com.example.demo.repository;

import com.example.demo.domain.CommunityRole;
import com.example.demo.domain.CommunityRoleType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface CommunityRoleRepository extends JpaRepository<CommunityRole, Long> {
    @Query("""
        SELECT cr.type FROM CommunityRole cr
        WHERE cr.user.id = :userId AND cr.community.id = :communityId
        """)
    Set<CommunityRoleType> findRoleTypesByUserIdAndCommunityId(Long userId, Long communityId);

    @Query("""
        SELECT cr.type FROM CommunityRole cr
        JOIN cr.community c
        JOIN c.post p
        WHERE cr.user.id = :userId AND p.id = :postId
        """)
    Set<CommunityRoleType> findRoleTypesByUserIdAndPostId(Long userId, Long postId);
}
