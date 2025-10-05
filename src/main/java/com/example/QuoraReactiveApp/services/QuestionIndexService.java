package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.adapter.QuestionElasticDocumentAdapter;
import com.example.QuoraReactiveApp.models.Question;
import com.example.QuoraReactiveApp.models.QuestionElasticDocument;
import com.example.QuoraReactiveApp.repositories.QuestionDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class QuestionIndexService implements IQuestionIndexService{

    private final QuestionDocumentRepository questionDocumentRepository;

    @Override
    public Mono<Void> createQuestionIndex(Question question) {
        QuestionElasticDocument document = QuestionElasticDocumentAdapter.toEntity(question);

        return questionDocumentRepository.save(document)
                .doOnSuccess(savedDoc -> System.out.println("Successfully indexed question: " + savedDoc.getId()))
                .doOnError(error -> System.err.println("Failed to index question: " + question.getId() + " - " + error.getMessage()))
                .then(); // Completes the Mono without emitting the saved document
    }
}
