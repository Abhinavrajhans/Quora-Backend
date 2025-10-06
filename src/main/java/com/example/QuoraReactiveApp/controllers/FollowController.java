package com.example.QuoraReactiveApp.controllers;


import com.example.QuoraReactiveApp.dto.FollowRequestDTO;
import com.example.QuoraReactiveApp.dto.FollowResponseDTO;
import com.example.QuoraReactiveApp.services.IFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final IFollowService followService;

    @PostMapping
    public Mono<FollowResponseDTO> createFollow(@RequestBody FollowRequestDTO followRequestDTO)
    {
        return followService.createFollow(followRequestDTO)
                .doOnSuccess(response-> System.out.println("Successfully created for follower "+ response.getFollowerId()+ " following "+ response.getFollowingId()))
                .doOnError(error-> System.out.println("Error Creating Follow"));
    }

    @GetMapping("/id/{id}")
    public Mono<FollowResponseDTO> findFollowById(@PathVariable String id)
    {
        return followService.findFollowById(id)
                .doOnSuccess(response-> System.out.println("Successfully found follower "+id+ " following "+response))
                .doOnError(error-> System.out.println("Error Finding Follower"));
    }

    @GetMapping("/follower/{id}")
    public Flux<FollowResponseDTO> findAllFollowersOfUserId(@PathVariable String id)
    {
        return followService.findAllFollowersOfUserId(id)
                .doOnNext(response -> System.out.println("Retrieved The follow :" + response))
                .doOnError(error -> System.out.println("Error getting the follow " + error))
                .doOnComplete(() -> System.out.println("Successfully got all the follow"));
    }

    @GetMapping("/following/{id}")
    public Flux<FollowResponseDTO> findAllFollowingsOfUserId(@PathVariable String id)
    {
        return followService.findAllFollowingOfUserId(id)
                .doOnNext(response -> System.out.println("Retrieved The following :" + response))
                .doOnError(error -> System.out.println("Error getting the following " + error))
                .doOnComplete(() -> System.out.println("Successfully got all the following"));
    }
}
