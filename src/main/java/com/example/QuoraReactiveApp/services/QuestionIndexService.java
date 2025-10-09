package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.adapter.QuestionElasticDocumentAdapter;
import com.example.QuoraReactiveApp.models.Question;
import com.example.QuoraReactiveApp.models.QuestionElasticDocument;
import com.example.QuoraReactiveApp.repositories.QuestionDocumentRepository;
import com.example.QuoraReactiveApp.repositories.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class QuestionIndexService implements IQuestionIndexService{

    private final QuestionDocumentRepository questionDocumentRepository;
    private final QuestionRepository questionRepository;

    @Override
    public Mono<Void> createQuestionIndex(Question question) {
        QuestionElasticDocument document = QuestionElasticDocumentAdapter.toEntity(question);

        return questionDocumentRepository.save(document)
                .doOnSuccess(savedDoc -> System.out.println("Successfully indexed question: " + savedDoc.getId()))
                .doOnError(error -> System.err.println("Failed to index question: " + question.getId() + " - " + error.getMessage()))
                .then(); // Completes the Mono without emitting the saved document
    }


    @Override
    public Flux<QuestionElasticDocument> searchQuestionsByElasticSearch(String query){
        return questionDocumentRepository.findByTitleContainingOrContentContaining(query,query)
                .doOnNext(response -> System.out.println("All questions retrieved successfully"))
                .doOnError(error -> System.out.println("Error finding questions: " + error))
                .doOnComplete(() -> System.out.println("All questions retrieved successfully"));
    }

    @Override
    public Mono<Void> deleteAllQuestions() {
        return questionDocumentRepository.deleteAll()
                .doOnSuccess(response -> System.out.println("All questions deleted successfully"))
                .doOnError(error -> System.err.println("Failed to delete all questions"));
    }

    @Override
    public Mono<Void> deleteQuestionById(String questionId) {
       return questionDocumentRepository.deleteById(questionId)
               .doOnSuccess(response -> System.out.println("Deleted question: " + questionId))
               .doOnError(error -> System.err.println("Failed to delete question: " + questionId));
    }

}
