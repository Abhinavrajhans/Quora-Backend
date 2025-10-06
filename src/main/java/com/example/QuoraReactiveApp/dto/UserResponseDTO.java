package com.example.QuoraReactiveApp.dto;

import lombok.*;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private String id;
    private String username;
    private String email;
    private String bio;
    private Integer followerCount=0;
    private Integer followingCount=0;
    private LocalDateTime createdAt;
}
