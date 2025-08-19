package com.example.QuoraReactiveApp.controllers;


import com.example.QuoraReactiveApp.dto.QuestionRequestDTO;
import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import com.example.QuoraReactiveApp.services.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public Mono<QuestionResponseDTO> createQuestion(@RequestBody QuestionRequestDTO questionRequestDTO) {
        return questionService.createQuestion(questionRequestDTO)
                .doOnSuccess(response -> System.out.println("Question created successfully: " + response))
                .doOnError(error -> System.out.println("Error creating question: " + error));
    }

    @GetMapping("/{id}")
    public Mono<QuestionResponseDTO> getQuestionById(@PathVariable String id) {
        return this.questionService.getQuestionById(id)
                .doOnSuccess(response -> System.out.println("Question retrieved successfully: " + response))
                .doOnError(error -> System.out.println("Error getting question: " + error));
    }

    @GetMapping
    public Flux<QuestionResponseDTO> getAllQuestions() {
        return this.questionService.getAllQuestions()
                .doOnNext(response -> System.out.println("Questions retrieved successfully: " + response))
                .doOnError(error -> System.out.println("Error getting all questions: " + error));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteQuestionById(@PathVariable String id) {
        return this.questionService.DeleteQuestionById(id)
                .doOnSuccess(response -> System.out.println("Question deleted successfully: " + response))
                .doOnError(error -> System.out.println("Error deleting question: " + error));
    }

    @GetMapping("/search")
    public Flux<QuestionResponseDTO> searchQuestions(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "100") int pageSize
    ){
        return this.questionService.searchQuestions(query, offset, pageSize);
    }


    @GetMapping("/tag/{tag}")
    public Flux<QuestionResponseDTO> getQuestionsByTag(@PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ){
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
