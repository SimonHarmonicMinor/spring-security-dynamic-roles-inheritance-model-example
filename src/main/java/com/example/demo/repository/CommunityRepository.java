package com.example.demo.repository;

import com.example.demo.domain.Community;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {
}
