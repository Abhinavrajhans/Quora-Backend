package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.adapter.QuestionAdapter;
import com.example.QuoraReactiveApp.dto.QuestionRequestDTO;
import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import com.example.QuoraReactiveApp.models.Question;
import com.example.QuoraReactiveApp.repositories.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO questionRequestDTO) {
        Question question = Question.builder()
                .title(questionRequestDTO.getTitle())
                .content(questionRequestDTO.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

       return questionRepository.save(question)
                .map(QuestionAdapter::toDTO)
                .doOnSuccess(response -> System.out.println("Question created successfully: "+ response))
                .doOnError(error -> System.out.println("Error creating question: " + error));

    }
}
