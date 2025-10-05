package com.example.QuoraReactiveApp.repositories;

import com.example.QuoraReactiveApp.models.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TagRepository extends ReactiveMongoRepository<Tag, String> {

    Mono<Tag> findByName(String name);
    Mono<Tag> findByNameIgnoreCase(String name);
    Flux<Tag> findByNameContainingIgnoreCase(String name);
    Flux<Tag> findAllBy(Pageable pageable);

}
