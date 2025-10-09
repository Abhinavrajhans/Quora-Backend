package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.models.Question;
import com.example.QuoraReactiveApp.models.QuestionElasticDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IQuestionIndexService {
    public Mono<Void> createQuestionIndex(Question question);
    public Flux<QuestionElasticDocument> searchQuestionsByElasticSearch(String query);
    public Mono<Void> deleteAllQuestions();
    public Mono<Void> deleteQuestionById(String questionId);
}
