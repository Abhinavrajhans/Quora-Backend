package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.adapter.QuestionAdapter;
import com.example.QuoraReactiveApp.dto.QuestionRequestDTO;
import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import com.example.QuoraReactiveApp.models.Question;
import com.example.QuoraReactiveApp.repositories.QuestionRepository;
import com.example.QuoraReactiveApp.utils.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO questionRequestDTO) {
        Question question = Question.builder()
                .title(questionRequestDTO.getTitle())
                .content(questionRequestDTO.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

       return questionRepository.save(question)
                .map(QuestionAdapter::toDTO)
                .doOnSuccess(response -> System.out.println("Question created successfully: "+ response))
                .doOnError(error -> System.out.println("Error creating question: " + error));

    }

    @Override
    public Mono<QuestionResponseDTO> getQuestionById(String questionId) {
        Mono<Question> question=this.questionRepository.findById(questionId);
        return question.map(QuestionAdapter::toDTO)
                .doOnSuccess(response -> System.out.println("Question retrieved successfully: "+ response))
                .doOnError(error -> System.out.println("Error getting question: " + error));
    }

    @Override
    public Flux<QuestionResponseDTO> getAllQuestions() {
        return this.questionRepository.findAll().map(QuestionAdapter::toDTO)
                .doOnNext(response -> System.out.println("Questions retrieved successfully: "+ response))
                .doOnError(error -> System.out.println("Error getting all questions: " + error));
    }

    @Override
    public Mono<Void> DeleteQuestionById(String questionId) {
       return this.questionRepository.deleteById(questionId)
        .doOnSuccess(response->System.out.println("The Question Got Deleted Successfully " + response))
        .doOnError(error-> System.out.println("Got Error while Deleting the Question "+error));
    }

    @Override
    public Flux<QuestionResponseDTO> searchQuestions(String searchTerm, Integer offset, Integer pageSize) {

        return questionRepository.findByTitleOrContentContainingIgnoreCase(searchTerm, PageRequest.of(offset,pageSize))
                .map(QuestionAdapter::toDTO)
                .doOnError(error -> System.out.println("Error getting questions: " + error))
                .doOnComplete(() -> System.out.println("All questions retrieved successfully"));


    }

    @Override
    public Flux<QuestionResponseDTO> searchQuestionsByCursor(String cursor, int size) {
        // what we should check is if we again want a certain number of records then we should use the pageable object
        // but we don't want the offset then we can keep the offset as 0.
        // if we want the size the we can use again the pageable object.

        // now we have to check if they have passed the cursor to us or not , for that we have to create a cursor util.
        //now the most important thing is now the ordering should be done using createdAt.
        Pageable pageable = PageRequest.of(0,size);
        if(!CursorUtils.isValidCursor(cursor))
        {
            return questionRepository.findTop10ByOrderByCreatedAtAsc(pageable)
                    .map(QuestionAdapter::toDTO)
                    .doOnError(error -> System.out.println("Error getting questions: " + error))
                    .doOnComplete(() -> System.out.println("All questions retrieved successfully"));
        }
        else{
            LocalDateTime cursorTimeStamp = CursorUtils.parseCursor(cursor);
            return questionRepository.findByCreatedAtGreaterThanOrderByCreatedAtAsc(cursorTimeStamp,pageable)
                    .map(QuestionAdapter::toDTO)
                    .doOnError(error -> System.out.println("Error getting questions: " + error))
                    .doOnComplete(() -> System.out.println("All questions retrieved successfully"));
        }

    }


}
