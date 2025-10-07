package com.example.QuoraReactiveApp.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionResponseDTO {

    private String id;
    private String title;
    private String content;
    private List<String> tagIds;
    private List<TagResponseDTO> tags; //Poplutatted tags for convenience
    private UserResponseDTO createdByUser;
    private LocalDateTime createdAt;

}
