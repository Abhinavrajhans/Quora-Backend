package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.dto.CommentRequestDTO;
import com.example.QuoraReactiveApp.dto.CommentResponseDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICommentService {

    Mono<CommentResponseDTO> createComment(CommentRequestDTO commentRequestDTO);
    Mono<CommentResponseDTO> findCommentById(String id);
    Flux<CommentResponseDTO> findAllComments(int page ,int size);
}
