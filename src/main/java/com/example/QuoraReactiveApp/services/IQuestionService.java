package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.dto.QuestionRequestDTO;
import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import com.example.QuoraReactiveApp.models.TagFilterType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IQuestionService {
    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO questionRequestDTO);
    public Mono<QuestionResponseDTO> getQuestionById(String questionId);
    public Flux<QuestionResponseDTO> getAllQuestions();
    public Mono<Void> deleteQuestionById(String questionId);
    public Flux<QuestionResponseDTO> searchQuestions(String searchTerm,Integer offset,Integer pageSize);
    public Flux<QuestionResponseDTO> searchQuestionsByCursor(String cursor,int size);
    public Flux<QuestionResponseDTO> getQuestionsByTags(List<String> tagIds, TagFilterType tagFilter, int page , int size);
    public Flux<QuestionResponseDTO> getQuestionsByTagId(String tagId,int page,int size);
    public Flux<QuestionResponseDTO> getQuestionsByAnyTags(List<String> tagIds, int page, int size);
    public Flux<QuestionResponseDTO> getQuestionsByAllTags(List<String> tagIds,int page,int size);

}
