package com.example.QuoraReactiveApp.adapter;

import com.example.QuoraReactiveApp.dto.FollowRequestDTO;
import com.example.QuoraReactiveApp.dto.FollowResponseDTO;
import com.example.QuoraReactiveApp.models.Follow;

public class FollowAdapter {

    public static Follow toEntity(FollowRequestDTO followRequestDTO) {
        return Follow.builder()
                .followerId(followRequestDTO.getFollowerId())
                .followingId(followRequestDTO.getFollowingId())
                .build();
    }

    public static FollowResponseDTO toDTO(Follow follow) {
        return FollowResponseDTO.builder()
                .id(follow.getId())
                .followerId(follow.getFollowerId())
                .followingId(follow.getFollowingId())
                .createdAt(follow.getCreatedAt())
                .build();


    }


}
