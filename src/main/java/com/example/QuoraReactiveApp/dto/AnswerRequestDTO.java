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
public class AnswerRequestDTO {

    @NotBlank(message = "Content is required")
    @Size(min=10,max=1000,message="Content must be betweem 10 and 1000 characters")
    private String content;

    @NotBlank(message = "Question ID is required")
    private String questionId;

    @NotBlank(message="CreatedById is required")
    private String createdById;


}
