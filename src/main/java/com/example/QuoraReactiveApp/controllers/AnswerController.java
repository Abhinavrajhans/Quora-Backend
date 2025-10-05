package com.example.QuoraReactiveApp.controllers;


import com.example.QuoraReactiveApp.dto.AnswerRequestDTO;
import com.example.QuoraReactiveApp.dto.AnswerResponseDTO;
import com.example.QuoraReactiveApp.services.IAnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final IAnswerService answerService;

    @PostMapping
    public Mono<AnswerResponseDTO> createAnswer(@Valid @RequestBody AnswerRequestDTO answerRequestDTO)
    {
        return  answerService.createAnswer(answerRequestDTO)
                .doOnSuccess(response -> System.out.println("Answer created successfully: "+ response))
                .doOnError(error -> System.out.println("Answer created failed: " + error));
    }


    @GetMapping("/{id}")
    public Mono<AnswerResponseDTO> findAnswerById(@PathVariable  String id)
    {
        return answerService.findAnswerById(id)
                .doOnSuccess(response -> System.out.println("Answer found successfully: "+ response))
                .doOnError(error -> System.out.println("Answer found failed: " + error));
    }

    @GetMapping("/question/{id}")
    public Flux<AnswerResponseDTO> findAnswerByQuestionId(@PathVariable String id)
    {
        return answerService.findAllAnswersByQuestionId(id)
                .doOnNext(response -> System.out.println("Answer found successfully: "+ response))
                .doOnError(error -> System.out.println("Answer found failed: " + error))
                .doOnComplete(() -> System.out.println("All answers found successfully"));


    }

}
