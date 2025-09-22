package com.example.QuoraReactiveApp.repositories;


import com.example.QuoraReactiveApp.models.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface QuestionRepository extends ReactiveMongoRepository<Question,String> {

    @Query("{ $or :  [ { title : { $regex: ?0 , $options:  'i'} } , { content :  { $regex: ?0 , $options:  'i'} }]}")
    Flux<Question> findByTitleOrContentContainingIgnoreCase(String searchTerm, Pageable pageable); // we need the pass the regex as the search term which is the 0th positonal parameter.

    Flux<Question> findByCreatedAtGreaterThanOrderByCreatedAtAsc(LocalDateTime cursor, Pageable pageable);

    Flux<Question> findTop10ByOrderByCreatedAtAsc(Pageable pageable); // just return the top 10 records

    //Find Questions by single tag ID
    @Query("{ 'tagIds' :  ?0}")
    Flux<Question> findByTagId(String tagId,Pageable pageable);

    //Find questions by multiple tag IDs (questions that have ANY of these tags)
    @Query("{ 'tagIds' : { $in :  ?0 } }")
    Flux<Question> findByTagIdIn(List<String> tagIds, Pageable pageable);

    // Find questions by muliple tag IDs (questions that have ALL of these tags)
    @Query("{ 'tagIds' :  {$all: ?0 }}")
    Flux<Question> findByTagIdAll(List<String> tagIds , Pageable pageable);



}
