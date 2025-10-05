package com.example.QuoraReactiveApp.repositories;

import com.example.QuoraReactiveApp.models.QuestionElasticDocument;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

public interface QuestionDocumentRepository extends ReactiveElasticsearchRepository<QuestionElasticDocument,String> {

    Flux<QuestionElasticDocument> findByTitleContainingOrContentContaining(String title, String content);
}
