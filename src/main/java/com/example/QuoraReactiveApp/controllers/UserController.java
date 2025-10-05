package com.example.QuoraReactiveApp.controllers;

import com.example.QuoraReactiveApp.dto.UserRequestDTO;
import com.example.QuoraReactiveApp.dto.UserResponseDTO;
import com.example.QuoraReactiveApp.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public Mono<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO)
    {
        return userService.createUser(userRequestDTO)
                .doOnSuccess(response -> System.out.println("User created successfully"))
                .doOnError(error -> System.out.println("Error while creating user"));
    }

    @GetMapping("/id/{id}")
    public Mono<UserResponseDTO> findUserById(@PathVariable String id)
    {
        return userService.findUserById(id)
                .doOnSuccess(response -> System.out.println("User found successfully"))
                .doOnError(error -> System.out.println("Error while finding user"));
    }

    @GetMapping
    public Flux<UserResponseDTO> findAllUser(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue ="10")int size
    ){
        return userService.findAllUsers(page,size)
                .doOnNext(response -> System.out.println("User found successfully"))
                .doOnError(error -> System.out.println("Error while finding user"))
                .doOnComplete(() -> System.out.println("All Users are Fetched Successfully"));
    }



}
