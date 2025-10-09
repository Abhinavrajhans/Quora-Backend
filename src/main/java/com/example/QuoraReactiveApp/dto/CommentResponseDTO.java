package com.example.QuoraReactiveApp.dto;

import com.example.QuoraReactiveApp.models.Type.CommentType;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {

    private String id;
    private String text;
    private String targetId;
    private CommentType targetType;
    private UserResponseDTO user;
    private LocalDateTime createdAt;
}
