package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.dto.FollowRequestDTO;
import com.example.QuoraReactiveApp.dto.FollowResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IFollowService {

    public Mono<FollowResponseDTO> createFollow(FollowRequestDTO followRequestDTO);
    public Mono<FollowResponseDTO> findFollowById(String id);
    public Flux<FollowResponseDTO> findAllFollowersByUserId(String userId);
    public Flux<FollowResponseDTO> findAllFollowingByUserId(String userId);
}
