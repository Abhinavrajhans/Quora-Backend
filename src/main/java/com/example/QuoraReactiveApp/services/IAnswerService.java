package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.dto.AnswerRequestDTO;
import com.example.QuoraReactiveApp.dto.AnswerResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAnswerService {
    public Mono<AnswerResponseDTO> createAnswer(AnswerRequestDTO answer);
    public Mono<AnswerResponseDTO> getAnswerById(String id);
    Flux<AnswerResponseDTO> getAllAnswersByQuestionId(String questionId);
}
