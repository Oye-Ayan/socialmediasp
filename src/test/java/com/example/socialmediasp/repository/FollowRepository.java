package com.example.socialmediasp.repository;

import com.example.socialmediasp.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // Get all followers of a user (people who follow userId)
    List<Follow> findByFollowingId(Long followingId);

    // Get all users that userId is following
    List<Follow> findByFollowerId(Long followerId);

    // Check if already following
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // For unfollow feature
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // Count followers
    long countByFollowingId(Long followingId);

    // Count following
    long countByFollowerId(Long followerId);
}