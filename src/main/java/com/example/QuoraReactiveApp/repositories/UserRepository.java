package com.example.QuoraReactiveApp.repositories;

import com.example.QuoraReactiveApp.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface UserRepository extends ReactiveMongoRepository<User,String> {

    Flux<User> findUserBy(Pageable pageable);
}
