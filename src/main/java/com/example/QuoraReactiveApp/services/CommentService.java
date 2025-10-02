package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.adapter.CommentAdapter;
import com.example.QuoraReactiveApp.dto.CommentRequestDTO;
import com.example.QuoraReactiveApp.dto.CommentResponseDTO;
import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
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

    @Override
    public Mono<CommentResponseDTO> createComment(CommentRequestDTO commentRequestDTO) {
            Comment comment = CommentAdapter.toEntity(commentRequestDTO);
            return commentRepository.save(comment)
                    .map(CommentAdapter::toDTO)
                    .doOnSuccess(response -> System.out.println("Comment created successfully: "+ response))
                    .doOnError(error -> System.out.println("Comment created failed: " + error));
    }

    @Override
    public Mono<CommentResponseDTO> getCommentById(String id)
    {
        return commentRepository.findById(id)
                .map(CommentAdapter::toDTO)
                .doOnSuccess(response -> System.out.println("Comment found successfully: "+ response))
                .doOnError(error -> System.out.println("Comment found failed: " + error));
    }

    @Override
    public Flux<CommentResponseDTO> getAllComments(int page,int size)
    {
            Pageable pageable = PageRequest.of(page,size);
            return commentRepository.getAllBy(pageable)
                    .map(CommentAdapter::toDTO)
                    .doOnNext(response -> System.out.println("Comment found successfully: "+ response))
                    .doOnError(error -> System.out.println("Comment found failed: " + error))
                    .doOnComplete(() -> System.out.println("All comments found successfully"));
    }



}
