package com.example.QuoraReactiveApp.adapter;

import com.example.QuoraReactiveApp.dto.UserRequestDTO;
import com.example.QuoraReactiveApp.dto.UserResponseDTO;
import com.example.QuoraReactiveApp.models.User;

public class UserAdapter {

    public static UserResponseDTO toDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .followerCount(user.getFollowerCount())
                .followingCount(user.getFollowingCount())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static User toEntity(UserRequestDTO userRequestDTO)
    {
        return User.builder()
                .username(userRequestDTO.getUsername())
                .email(userRequestDTO.getEmail())
                .bio(userRequestDTO.getBio())
                .followerCount(0)
                .followingCount(0)
                .build();
    }
}
