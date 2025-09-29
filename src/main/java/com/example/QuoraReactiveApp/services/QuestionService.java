package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.adapter.QuestionAdapter;
import com.example.QuoraReactiveApp.dto.QuestionRequestDTO;
import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import com.example.QuoraReactiveApp.models.Question;
import com.example.QuoraReactiveApp.models.TagFilterType;
import com.example.QuoraReactiveApp.repositories.QuestionRepository;
import com.example.QuoraReactiveApp.utils.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;
    private final TagService tagService;


    @Override
    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO questionRequestDTO)
    {
        Question question = QuestionAdapter.toEntity(questionRequestDTO);
        return questionRepository.save(question)
                .flatMap(savedQuestion->{
                    //Increment usage count for all the tags
                    if(savedQuestion.getTagIds()!=null && !savedQuestion.getTagIds().isEmpty()){
                        return Flux.fromIterable(savedQuestion.getTagIds())
                                .flatMap(tagService::incrementUsageCount)
                                .then(Mono.just(savedQuestion));
                    }
                    return Mono.just(savedQuestion);
                })
                .flatMap(this::enrichQuestionWithTags)
                .doOnNext(response -> System.out.println("Question created Successfully" + response))
                .doOnError(throwable -> System.out.println("Question created Failed" + throwable));
    }

    @Override
    public Mono<QuestionResponseDTO> getQuestionById(String questionId) {
        return questionRepository.findById(questionId)
                .flatMap(this::enrichQuestionWithTags)
                .doOnSuccess(response -> System.out.println("Question retrieved successfully: " + response))
                .doOnError(error -> System.out.println("Error getting question: " + error));
    }

    @Override
    public Flux<QuestionResponseDTO> getAllQuestions() {
        return this.questionRepository.findAll().map(QuestionAdapter::toDTO)
                .doOnNext(response -> System.out.println("Questions retrieved successfully: "+ response))
                .doOnError(error -> System.out.println("Error getting all questions: " + error));
    }


    @Override
    public Mono<Void> deleteQuestionById(String questionId) {
        return this.questionRepository.findById(questionId)
                .flatMap(foundQuestion -> {
                    if (foundQuestion.getTagIds() != null && !foundQuestion.getTagIds().isEmpty()) {
                        return Flux.fromIterable(foundQuestion.getTagIds())
                                .flatMap(tagService::decrementUsageCount)
                                .then(this.questionRepository.deleteById(questionId));
                    }
                    return this.questionRepository.deleteById(questionId);
                })
                .doOnSuccess(ignored ->
                        System.out.println("✅ The Question with ID " + questionId + " got deleted successfully"))
                .doOnError(error ->
                        System.err.println("❌ Error while deleting question " + questionId + ": " + error.getMessage()));
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

    @Override
    public Flux<QuestionResponseDTO> getQuestionsByTags(List<String> tagIds, TagFilterType filterType, int page, int size) {
        if(tagIds == null || tagIds.isEmpty())return Flux.empty();
        Pageable pageable = PageRequest.of(page,size);

        //choose the appropriate repository method based on filter type
        Flux<Question> questionsFlux=switch(filterType){
            case SINGLE->  questionRepository.findByTagId(tagIds.getFirst(),pageable);
            case ANY -> questionRepository.findByTagIdIn(tagIds,pageable);
            case ALL -> questionRepository.findByTagIdAll(tagIds,pageable);
        };

        return questionsFlux
                .flatMap(this::enrichQuestionWithTags)
                .doOnNext(response -> System.out.println("Question by tags retrieved successfully: " + response))
                .doOnError(error -> System.out.println("Error getting questions by tags: " + error))
                .doOnComplete(() -> System.out.println("All questions by tags retrieved successfully"));

    }

    private Mono<QuestionResponseDTO> enrichQuestionWithTags(Question question){
        if(question.getTagIds() == null || question.getTagIds().isEmpty()){
            return Mono.just(QuestionAdapter.toDTO(question));
        }

        return Flux.fromIterable(question.getTagIds())
                .flatMap(tagService::getTagById)
                .collectList()
                .map(tagList -> QuestionAdapter.toDTOWithTags(question,tagList));
    }

    @Override
    public Flux<QuestionResponseDTO> getQuestionsByTagId(String tagId,int page,int size){
       return getQuestionsByTags(List.of(tagId),TagFilterType.SINGLE,page,size);
    }

    @Override
    public Flux<QuestionResponseDTO> getQuestionsByAnyTags(List<String> tagIds, int page, int size) {
        return getQuestionsByTags(tagIds, TagFilterType.ANY, page, size);
    }

    @Override
    public Flux<QuestionResponseDTO> getQuestionsByAllTags(List<String> tagIds, int page, int size) {
        return getQuestionsByTags(tagIds, TagFilterType.ALL, page, size);
    }


}
