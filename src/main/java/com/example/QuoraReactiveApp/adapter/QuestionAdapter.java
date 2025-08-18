package com.example.QuoraReactiveApp.adapter;

import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import com.example.QuoraReactiveApp.models.Question;

public class QuestionAdapter {

    public static QuestionResponseDTO toDTO(Question question) {
        return QuestionResponseDTO.builder()
                .id(question.getId())
                .title(question.getTitle())
                .context(question.getContent())
                .createdAt(question.getCreatedAt())
                .build();
    }
}
