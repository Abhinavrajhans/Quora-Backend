package com.example.QuoraReactiveApp.models;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection="likes") // this is same as @Entity in SQL but here we have mongoDB
public class Like {

    @Id
    private String id;

    @NotBlank(message = "The targetId is Required.")
    private String targetId;

    @NotNull(message = "The likeType is Required.")
    private LikeType likeType; // QUESTION , ANSWER

    @NotNull(message = "The isLike is Required.")
    private Boolean isLike; // it can denotes whether this is a like or dislike

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
