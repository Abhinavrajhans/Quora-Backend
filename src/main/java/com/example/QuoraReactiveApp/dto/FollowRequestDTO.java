package com.example.QuoraReactiveApp.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowRequestDTO {

    @NotBlank(message="Follower Id is required")
    private String followerId;

    @NotBlank(message="Following Id is required")
    private String followingId;
}
