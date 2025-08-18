package com.example.QuoraReactiveApp.repositories;

import com.example.QuoraReactiveApp.models.Question;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


public interface QuestionRepository extends ReactiveMongoRepository<Question,String> {


}
