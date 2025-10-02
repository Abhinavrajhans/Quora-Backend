package com.example.QuoraReactiveApp.adapter;

import com.example.QuoraReactiveApp.dto.LikeRequestDTO;
import com.example.QuoraReactiveApp.dto.LikeResponseDTO;
import com.example.QuoraReactiveApp.models.Like;

public class LikeAdapter {

    public static LikeResponseDTO toDTO(Like like)
    {
        return LikeResponseDTO.builder()
                .id(like.getId())
                .targetId(like.getTargetId())
                .likeType(like.getLikeType())
                .isLike(like.getIsLike())
                .createdDate(like.getCreatedAt())
                .build();
    }
    public static Like toEntity(LikeRequestDTO dto)
    {
        return Like.builder()
                .targetId(dto.getTargetId())
                .likeType(dto.getLikeType())
                .isLike(dto.getIsLike())
                .build();
    }
}
