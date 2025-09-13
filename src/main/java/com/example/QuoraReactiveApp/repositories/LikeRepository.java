package com.example.QuoraReactiveApp.repositories;

import com.example.QuoraReactiveApp.models.Like;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface LikeRepository extends ReactiveMongoRepository<Like,String> {

}
