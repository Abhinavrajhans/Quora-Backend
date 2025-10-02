package com.example.QuoraReactiveApp.controllers;

import com.example.QuoraReactiveApp.dto.LikeRequestDTO;
import com.example.QuoraReactiveApp.dto.LikeResponseDTO;
import com.example.QuoraReactiveApp.models.Like;
import com.example.QuoraReactiveApp.repositories.LikeRepository;
import com.example.QuoraReactiveApp.services.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public Mono<LikeResponseDTO> createLike(@Valid @RequestBody LikeRequestDTO like) {
        return likeService.createLike(like)
                .doOnSuccess(response -> System.out.println("Like is Created Successfully: "+ response))
                .doOnError(error -> System.out.println("Like creation Failed: "+ error));
    }

    @GetMapping("/{id}")
    public Mono<LikeResponseDTO> getLikeById(@PathVariable  String id)
    {
        return likeService.getLikeById(id)
                .doOnSuccess(response -> System.out.println("Like of id : "+ id +" is retrieved successfully"))
                .doOnError(error -> System.out.println("Like of id : "+ id +" retrival failed"));
    }
}
