package com.example.QuoraReactiveApp.repositories;

import com.example.QuoraReactiveApp.models.UserFeed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserFeedRepository extends ReactiveMongoRepository<UserFeed, String> {

    // Get paginated feed for a user, newest questions first
    Flux<UserFeed> findByUserIdOrderByQuestionCreatedAtDesc(String userId, Pageable pageable);

    // Clean up feed entries when a question is deleted
    Mono<Void> deleteAllByQuestionId(String questionId);
}
