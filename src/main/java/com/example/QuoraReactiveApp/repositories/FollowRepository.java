package com.example.QuoraReactiveApp.repositories;

import com.example.QuoraReactiveApp.dto.FollowRequestDTO;
import com.example.QuoraReactiveApp.dto.FollowResponseDTO;
import com.example.QuoraReactiveApp.models.Follow;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface FollowRepository extends ReactiveMongoRepository<Follow,String> {

    // 1️⃣ Get all followers of a user (people who follow this user)
    Flux<FollowResponseDTO> findByFollowerId(String user_id);


    // 2️⃣ Get all users a given user is following
    Flux<FollowResponseDTO> findByFollowingId(String user_id);

}
