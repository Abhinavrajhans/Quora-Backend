package com.example.QuoraReactiveApp.dto;

import lombok.*;

import java.time.LocalDateTime;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionResponseDTO {

    private String id;

    private String title;

    private String context;

    private LocalDateTime createdAt;

}
