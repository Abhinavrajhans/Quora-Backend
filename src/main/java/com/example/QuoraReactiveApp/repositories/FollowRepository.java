package com.example.QuoraReactiveApp.repositories;

import com.example.QuoraReactiveApp.dto.FollowResponseDTO;
import com.example.QuoraReactiveApp.models.Follow;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface FollowRepository extends ReactiveMongoRepository<Follow,String> {

    // Get all followers of a user (people who follow this user)
    // Find records in the follow table where followingId = userId (this user is being followed)
    Flux<FollowResponseDTO> findByFollowingId(String userId);

    // Get all users that a given user is following
    // Find records where followerId = user_id (this user is the follower)
    Flux<FollowResponseDTO> findByFollowerId(String userId);
}