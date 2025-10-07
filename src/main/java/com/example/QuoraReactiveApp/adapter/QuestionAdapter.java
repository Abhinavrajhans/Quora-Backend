package com.example.QuoraReactiveApp.adapter;

import com.example.QuoraReactiveApp.dto.QuestionRequestDTO;
import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import com.example.QuoraReactiveApp.dto.TagResponseDTO;
import com.example.QuoraReactiveApp.dto.UserResponseDTO;
import com.example.QuoraReactiveApp.models.Question;

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

    public static QuestionResponseDTO toDTO(Question question,UserResponseDTO user) {
        return QuestionResponseDTO.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .tagIds(question.getTagIds())
                .createdByUser(user)
                .createdAt(question.getCreatedAt())
                .build();
    }

    public static QuestionResponseDTO toDTOWithTagsAndUser(Question question, List<TagResponseDTO> tags, UserResponseDTO user)
    {
        return QuestionResponseDTO.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .tagIds(question.getTagIds())
                .tags(tags)
                .createdByUser(user)
                .createdAt(question.getCreatedAt())
                .build();
    }

    public static Question toEntity(QuestionRequestDTO questionRequestDTO) {
        return Question.builder()
                .title(questionRequestDTO.getTitle())
                .content(questionRequestDTO.getContent())
                .tagIds(questionRequestDTO.getTagIds())
                .createdById(questionRequestDTO.getCreatedById())
                .build();
    }
}
