package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.models.Question;
import reactor.core.publisher.Mono;

public interface IQuestionIndexService {
    public Mono<Void> createQuestionIndex(Question question);
}
