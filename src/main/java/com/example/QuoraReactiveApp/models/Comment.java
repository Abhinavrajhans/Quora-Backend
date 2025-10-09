package com.example.QuoraReactiveApp.models;

import com.example.QuoraReactiveApp.models.Type.CommentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection="comments") // this is same as @Entity in SQL but here we have mongoDB
public class Comment {

    @Id
    private String id;

    @NotBlank(message="Text is required")
    @Size(min=2, max=1000 , message="The comment must be between 10 and 1000 Characters.")
    private String text;

    @NotBlank(message="Target Id is required")
    private String targetId;

    @NotNull(message="Target Type is required")
    private CommentType targetType;

    @NotBlank(message="CreatedById is required")
    private String createdById;

    @Builder.Default
    private Integer views=0;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
