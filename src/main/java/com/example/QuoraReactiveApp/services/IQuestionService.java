package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.dto.QuestionRequestDTO;
import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import com.example.QuoraReactiveApp.models.QuestionElasticDocument;
import com.example.QuoraReactiveApp.models.Type.TagFilterType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IQuestionService {
    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO questionRequestDTO);
    public Mono<QuestionResponseDTO> findQuestionById(String questionId);
    public Flux<QuestionResponseDTO> findAllQuestions();
    public Mono<Void> deleteQuestionById(String questionId);
    public Flux<QuestionResponseDTO> searchQuestions(String searchTerm,Integer offset,Integer pageSize);
    public Flux<QuestionResponseDTO> searchQuestionsByCursor(String cursor,int size);
    public Flux<QuestionResponseDTO> findQuestionsByTags(List<String> tagIds, TagFilterType tagFilter, int page , int size);
    public Flux<QuestionResponseDTO> findQuestionsByTagId(String tagId,int page,int size);
    public Flux<QuestionResponseDTO> findQuestionsByAnyTags(List<String> tagIds, int page, int size);
    public Flux<QuestionResponseDTO> findQuestionsByAllTags(List<String> tagIds,int page,int size);
    public Flux<QuestionElasticDocument> searchQuestionsByElasticSearch(String query);

}
