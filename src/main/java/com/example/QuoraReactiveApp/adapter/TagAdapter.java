package com.example.QuoraReactiveApp.adapter;

import com.example.QuoraReactiveApp.dto.TagRequestDTO;
import com.example.QuoraReactiveApp.dto.TagResponseDTO;
import com.example.QuoraReactiveApp.models.Tag;


public class TagAdapter {

    public static TagResponseDTO toDTO(Tag tag)
    {
        return TagResponseDTO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .description(tag.getDescription())
                .usageCount(tag.getUsageCount())
                .createdAt(tag.getCreatedAt())
                .build();
    }

    public static Tag toEntity(TagRequestDTO tagRequestDTO)
    {
        return Tag.builder()
                .name(tagRequestDTO.getName())
                .description(tagRequestDTO.getDescription())
                .usageCount(0)
                .build();

    }
}
