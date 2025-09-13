package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.adapter.AnswerAdapter;
import com.example.QuoraReactiveApp.dto.AnswerRequestDTO;
import com.example.QuoraReactiveApp.dto.AnswerResponseDTO;
import com.example.QuoraReactiveApp.models.Answer;
import com.example.QuoraReactiveApp.repositories.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnswerService implements IAnswerService{

    private final AnswerRepository answerRepository;

    @Override
    public Mono<AnswerResponseDTO> createAnswer(AnswerRequestDTO answerRequestDTO){
        Answer answer =  AnswerAdapter.toEntity(answerRequestDTO);
        return answerRepository.save(answer)
                .map(AnswerAdapter::toDTO)
                .doOnSuccess(response -> System.out.println("Answer created successfully: "+ response))
                .doOnError(error -> System.out.println("Answer creation failed: " + error));
    }


    @Override
    public Mono<AnswerResponseDTO> getAnswerById(String id){
        return answerRepository.findById(id).map(AnswerAdapter::toDTO)
                .doOnSuccess(response -> System.out.println("Answer found successfully: "+ response))
                .doOnError(error -> System.out.println("Answer found failed: " + error));
    }

    @Override
    public Flux<AnswerResponseDTO> getAllAnswersByQuestionId(String questionId) {
        return answerRepository.findByQuestionId(questionId)
                .map(AnswerAdapter::toDTO)
                .doOnNext(response -> System.out.println("Answer found successfully: "+ response))
                .doOnError(error -> System.out.println("Answer found failed: " + error))
                .doOnComplete(() -> System.out.println("All answers found successfully"));
    }


}
