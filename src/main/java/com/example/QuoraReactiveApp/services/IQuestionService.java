package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.dto.QuestionRequestDTO;
import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import com.example.QuoraReactiveApp.models.Question;
import reactor.core.publisher.Mono;

public interface IQuestionService {
    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO questionRequestDTO);
}
