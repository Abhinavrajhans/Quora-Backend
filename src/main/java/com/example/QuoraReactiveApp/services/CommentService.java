package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.adapter.CommentAdapter;
import com.example.QuoraReactiveApp.adapter.UserAdapter;
import com.example.QuoraReactiveApp.dto.CommentRequestDTO;
import com.example.QuoraReactiveApp.dto.CommentResponseDTO;
import com.example.QuoraReactiveApp.dto.UserResponseDTO;
import com.example.QuoraReactiveApp.models.Comment;
import com.example.QuoraReactiveApp.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService{

    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    public Mono<CommentResponseDTO> createComment(CommentRequestDTO commentRequestDTO) {
            Comment comment = CommentAdapter.toEntity(commentRequestDTO);
            return commentRepository.save(comment)
                    .flatMap(this::enrichCommentWithUser)
                    .doOnSuccess(response -> System.out.println("Comment created successfully: "+ response))
                    .doOnError(error -> System.out.println("Comment creation failed: " + error));
    }

    @Override
    public Mono<CommentResponseDTO> findCommentById(String id)
    {
        return commentRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Comment with Id "+ id + " Not Found.")))
                .flatMap(this::enrichCommentWithUser)
                .doOnSuccess(response -> System.out.println("Comment found successfully: "+ response))
                .doOnError(error -> System.out.println("Comment found failed: " + error));
    }

    @Override
    public Flux<CommentResponseDTO> findAllComments(int page,int size)
    {
            Pageable pageable = PageRequest.of(page,size);
            return commentRepository.findAllBy(pageable)
                    .flatMap(this::enrichCommentWithUser)
                    .doOnNext(response -> System.out.println("Comment found successfully: "+ response))
                    .doOnError(error -> System.out.println("Comment found failed: " + error))
                    .doOnComplete(() -> System.out.println("All comments found successfully"));
    }

    public Mono<CommentResponseDTO> enrichCommentWithUser(Comment comment)
    {
        return userService.findUserById(comment.getCreatedById())
                .map(userResponseDTO-> CommentAdapter.toDTO(comment,userResponseDTO));
    }
    
}
