package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.dto.QuestionRequestDTO;
import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IQuestionService {
    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO questionRequestDTO);
    public Mono<QuestionResponseDTO> getQuestionById(String questionId);
    public Flux<QuestionResponseDTO> getAllQuestions();
    public Mono<Void> DeleteQuestionById(String questionId);
    public Flux<QuestionResponseDTO> searchQuestions(String searchTerm,Integer offset,Integer pageSize);
    public Flux<QuestionResponseDTO> searchQuestionsByCursor(String cursor,int size);

}
