package com.example.QuoraReactiveApp.controllers;


import com.example.QuoraReactiveApp.dto.QuestionRequestDTO;
import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;

import com.example.QuoraReactiveApp.models.QuestionElasticDocument;
import com.example.QuoraReactiveApp.services.IQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final IQuestionService questionService;

    @PostMapping
    public Mono<QuestionResponseDTO> createQuestion(@Valid @RequestBody QuestionRequestDTO questionRequestDTO) {
        return questionService.createQuestion(questionRequestDTO)
                .doOnSuccess(response -> System.out.println("Question created successfully: " + response))
                .doOnError(error -> System.out.println("Error creating question: " + error));
    }

    @GetMapping("/{id}")
    public Mono<QuestionResponseDTO> findQuestionById(@PathVariable String id) {
        return this.questionService.findQuestionById(id)
                .doOnSuccess(response -> System.out.println("Question retrieved successfully: " + response))
                .doOnError(error -> System.out.println("Error Finding question: " + error));
    }

    @GetMapping
    public Flux<QuestionResponseDTO> findAllQuestions() {
        return this.questionService.findAllQuestions()
                .doOnNext(response -> System.out.println("Questions retrieved successfully: " + response))
                .doOnError(error -> System.out.println("Error finding all questions: " + error));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteQuestionById(@PathVariable String id) {
        return this.questionService.deleteQuestionById(id)
                .doOnSuccess(response -> System.out.println("Question deleted successfully: " + response))
                .doOnError(error -> System.out.println("Error deleting question: " + error));
    }

    @GetMapping("/search")
    public Flux<QuestionResponseDTO> searchQuestions(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "100") int pageSize
    ){
        return this.questionService.searchQuestions(query, offset, pageSize)
                .doOnNext(response -> System.out.println("Questions retrieved successfully: " + response))
                .doOnError(error -> System.out.println("Error finding all questions: " + error))
                .doOnComplete(() -> System.out.println("All questions retrieved successfully"));
    }


    @GetMapping("/cursor")
    public Flux<QuestionResponseDTO> searchQuestionsByCursor(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue="10") int size
    ) {
        return this.questionService.searchQuestionsByCursor(cursor, size)
                .doOnComplete(()-> System.out.println("Questions retrieved successfully: " + cursor))
                .doOnError(error -> System.out.println("Error finding all questions: " + error));
    }


    @GetMapping("/tag/{tagId}")
    public Flux<QuestionResponseDTO> findQuestionsByTagId(@PathVariable String tagId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ){
      return questionService.findQuestionsByTagId(tagId, page, size)
              .doOnNext(response -> System.out.println("Questions retrieved successfully: " + response))
              .doOnError(error -> System.out.println("Error finding all questions: " + error))
              .doOnComplete(() -> System.out.println("All questions retrieved successfully"));
    }


    @GetMapping("/tags/any")
    public Flux<QuestionResponseDTO> findingQuestionByAnyTags(
            @RequestParam List<String> tagIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return questionService.findQuestionsByAnyTags(tagIds, page, size)
                .doOnNext(response -> System.out.println("Questions retrieved successfully: " + response))
                .doOnError(error -> System.out.println("Error finding all questions: " + error))
                .doOnComplete(() -> System.out.println("All questions retrieved successfully"));
    }

    // Multiple tags with ALL logic (AND)
    @GetMapping("/tags/all")
    public Flux<QuestionResponseDTO> findQuestionsByAllTags(
            @RequestParam List<String> tagIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return questionService.findQuestionsByAllTags(tagIds, page, size)
                .doOnNext(response -> System.out.println("Questions retrieved successfully: " + response))
                .doOnError(error -> System.out.println("Error finding all questions: " + error))
                .doOnComplete(() -> System.out.println("All questions retrieved successfully"));
    }

    @GetMapping("/elasticsearch")
    public Flux<QuestionElasticDocument> searchQuestionByElasticSearch(
            @RequestParam String query
    ){
        return questionService.searchQuestionsByElasticSearch(query)
                .doOnNext(response -> System.out.println("Questions retrieved successfully: " + response))
                .doOnError(error -> System.out.println("Error finding all questions: " + error))
                .doOnComplete(() -> System.out.println("All questions retrieved successfully"));
    }
}
s