package com.example.QuoraReactiveApp.dto;

import com.example.QuoraReactiveApp.models.Type.CommentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {

    @NotBlank(message="The comment must be between 2 and 1000 characters.")
    @Size(min=2, max=1000 , message="The comment must be between 2 and 1000 characters")
    private String text;

    @NotBlank(message="The Target Id is required")
    private String targetId;

    @NotNull(message="The Target Types is required")
    private CommentType targetType;
}
