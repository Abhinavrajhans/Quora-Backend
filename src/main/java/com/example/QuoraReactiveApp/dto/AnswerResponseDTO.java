package com.example.QuoraReactiveApp.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerResponseDTO {

    private String id;
    private String content;
    private String questionId;
    private UserResponseDTO user;
    private LocalDateTime createdAt;

}
