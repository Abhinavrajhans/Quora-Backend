package com.example.QuoraReactiveApp.adapter;

import com.example.QuoraReactiveApp.dto.CommentRequestDTO;
import com.example.QuoraReactiveApp.dto.CommentResponseDTO;
import com.example.QuoraReactiveApp.models.Comment;


public class CommentAdapter {

    public static CommentResponseDTO toDTO(Comment comment){
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .text(comment.getText())
                .targetId(comment.getTargetId())
                .targetType(comment.getTargetType())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static Comment toEntity(CommentRequestDTO commentRequestDTO){
        return Comment.builder()
                .text(commentRequestDTO.getText())
                .targetId(commentRequestDTO.getTargetId())
                .targetType(commentRequestDTO.getTargetType())
                .build();
    }
}
