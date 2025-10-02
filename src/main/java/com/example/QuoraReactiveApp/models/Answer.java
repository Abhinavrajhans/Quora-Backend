package com.example.QuoraReactiveApp.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@Document(collection="answers") // this is same as @Entity in SQL but here we have mongoDB
public class Answer {

    @Id
    private String id;

    @NotBlank(message = "Content is Required.")
    @Size(min=10 , max=1000, message = "Content must be between 10 and 1000 characters.")
    private String content;

    //Now a Question can have multiple answer , but a answer belong to a question.
    @Indexed
    @NotBlank(message="Question Id is Required.")
    private String questionId;

    @Builder.Default
    private Integer views=0;

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
