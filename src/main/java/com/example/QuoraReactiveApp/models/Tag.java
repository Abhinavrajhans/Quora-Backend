package com.example.QuoraReactiveApp.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection="tags")
public class Tag {

    @Id
    private String id;

    @NotBlank(message="Tag name is required")
    @Size(min=2,max=50,message="Tag name must be between 2 and 50 characters")
    @Indexed(unique=true) // Ensure tag names are unique
    private String name;

    @Size(max=200 , message = "Description must not exceed 200 characters")
    private String description;

    @Builder.Default
    private Integer usageCount = 0; // Track how many questions use this tag

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
