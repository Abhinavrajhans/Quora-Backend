package com.example.QuoraReactiveApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min=10,max=100,message="Title must be betweem 10 and 100 characters")
    private String title;


    @NotBlank(message = "Content is required")
    @Size(min=10,max=1000,message="Content must be betweem 10 and 1000 characters")
    private String content;
}
