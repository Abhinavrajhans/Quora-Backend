package com.example.QuoraReactiveApp.adapter;

import com.example.QuoraReactiveApp.dto.QuestionRequestDTO;
import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import com.example.QuoraReactiveApp.dto.TagResponseDTO;
import com.example.QuoraReactiveApp.models.Question;

import java.time.LocalDateTime;
import java.util.List;

public class QuestionAdapter {

    public static QuestionResponseDTO toDTO(Question question) {
        return QuestionResponseDTO.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .tagIds(question.getTagIds())
                .createdAt(question.getCreatedAt())
                .build();
    }

    public static QuestionResponseDTO toDTOWithTags(Question question, List<TagResponseDTO> tags)
    {
        return QuestionResponseDTO.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .tagIds(question.getTagIds())
                .tags(tags)
                .createdAt(question.getCreatedAt())
                .build();
    }

    public static Question toEntity(QuestionRequestDTO questionRequestDTO) {
        return Question.builder()
                .title(questionRequestDTO.getTitle())
                .content(questionRequestDTO.getContent())
                .tagIds(questionRequestDTO.getTagIds())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
