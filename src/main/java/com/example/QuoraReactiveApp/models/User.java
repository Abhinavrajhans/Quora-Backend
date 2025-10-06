package com.example.QuoraReactiveApp.models;


import jakarta.validation.constraints.Email;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection="users")
public class User {

    @Id
    private String id;

    @NotBlank(message="username is required.")
    @Size(min=2 , max=100, message="username must be between 2 and 100 characters")
    @Indexed(unique=true)
    private String username;

    @NotBlank(message="email is required.")
    @Email(message="Email should be valid")
    @Indexed(unique=true)
    private String email;

    @Size(max=500, message="Bio can't exceed 500 characters")
    private String bio;

    @Builder.Default
    private Integer followerCount=0;

    @Builder.Default
    private Integer followingCount=0;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;



}
