package com.example.QuoraReactiveApp.repositories;

import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import com.example.QuoraReactiveApp.models.Question;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;


public interface QuestionRepository extends ReactiveMongoRepository<Question,String> {


}
