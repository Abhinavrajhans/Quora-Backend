package com.example.QuoraReactiveApp.adapter;

import com.example.QuoraReactiveApp.dto.AnswerRequestDTO;
import com.example.QuoraReactiveApp.dto.AnswerResponseDTO;
import com.example.QuoraReactiveApp.models.Answer;


public class AnswerAdapter {

    public static AnswerResponseDTO toDTO(Answer answer){
        return AnswerResponseDTO.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .questionId(answer.getQuestionId())
                .createdAt(answer.getCreatedAt())
                .build();
    }

    public static Answer toEntity(AnswerRequestDTO answer){
        return Answer.builder()
                .content(answer.getContent())
                .questionId(answer.getQuestionId())
                .build();
    }
}
