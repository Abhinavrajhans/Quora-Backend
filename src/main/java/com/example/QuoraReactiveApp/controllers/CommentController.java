package com.example.QuoraReactiveApp.controllers;


import com.example.QuoraReactiveApp.dto.CommentRequestDTO;
import com.example.QuoraReactiveApp.dto.CommentResponseDTO;
import com.example.QuoraReactiveApp.services.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public Mono<CommentResponseDTO> createComment(@Valid @RequestBody CommentRequestDTO commentRequestDTO)
    {
        return commentService.createComment(commentRequestDTO)
                .doOnSuccess(response -> System.out.println("Comment created successfully: " + response))
                .doOnError(error -> System.out.println("Comment created failed: " + error));
    }

    @GetMapping("/id/{id}")
    public Mono<CommentResponseDTO> findCommentById(@PathVariable String id)
    {
        return commentService.findCommentById(id)
                .doOnSuccess(response -> System.out.println("Comment of id : "+ id +" is retrieved successfully"))
                .doOnError(error -> System.out.println("comment of id : "+ id +" retrival failed"));
    }

    @GetMapping
    public Flux<CommentResponseDTO> findAllComments(
            @RequestParam(defaultValue="0")int page,
            @RequestParam(defaultValue="10") int size
    )
    {
        return commentService.findAllComments(page,size)
                .doOnNext(response -> System.out.println("All comments retrieved successfully"))
                .doOnError(error -> System.out.println("All comments retrieved failed: " + error))
                .doOnComplete(() -> System.out.println("All comments retrieved successfully"));
    }

}
