package com.example.QuoraReactiveApp.repositories;


import com.example.QuoraReactiveApp.models.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;



@Repository
public interface QuestionRepository extends ReactiveMongoRepository<Question,String> {

    @Query("{ $or :  [ { title : { $regex: ?0 , $options:  'i'} } , { content :  { $regex: ?0 , $options:  'i'} }]}")
    Flux<Question> findByTitleOrContentContainingIgnoreCase(String searchTerm, Pageable pageable); // we need the pass the regex as the search term which is the 0th positonal parameter.

}
