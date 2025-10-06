package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.dto.UserRequestDTO;
import com.example.QuoraReactiveApp.dto.UserResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserService {

    public Mono<UserResponseDTO> createUser(UserRequestDTO userRequestDTO);
    public Mono<UserResponseDTO> findUserById(String id);
    public Flux<UserResponseDTO> findAllUsers(int page ,int size);
    public Mono<UserResponseDTO> incrementFollowerCount(String id);
    public Mono<UserResponseDTO> incrementFollowingCount(String id);
}
