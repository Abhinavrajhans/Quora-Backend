package com.example.QuoraReactiveApp.repositories;

import com.example.QuoraReactiveApp.models.Tag;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends ReactiveMongoRepository<Tag, String> {

}
