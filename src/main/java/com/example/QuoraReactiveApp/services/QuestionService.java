package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.adapter.QuestionAdapter;
import com.example.QuoraReactiveApp.dto.QuestionRequestDTO;
import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import com.example.QuoraReactiveApp.dto.TagResponseDTO;
import com.example.QuoraReactiveApp.dto.UserResponseDTO;
import com.example.QuoraReactiveApp.events.QuestionCreatedEvent;
import com.example.QuoraReactiveApp.events.ViewCountEvent;
import com.example.QuoraReactiveApp.models.Question;
import com.example.QuoraReactiveApp.models.QuestionElasticDocument;
import com.example.QuoraReactiveApp.models.Type.TagFilterType;
import com.example.QuoraReactiveApp.producers.KafkaEventProducer;
import com.example.QuoraReactiveApp.repositories.QuestionRepository;
import com.example.QuoraReactiveApp.repositories.UserFeedRepository;
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
    private final KafkaEventProducer kafkaEventProducer;
    private final IQuestionIndexService questionIndexService;
    private final UserService userService;
    private final UserFeedRepository userFeedRepository;


    @Override
    public Mono<QuestionResponseDTO> createQuestion(QuestionRequestDTO questionRequestDTO) {
        return userService.findUserById(questionRequestDTO.getCreatedById())
                .switchIfEmpty(Mono.error(new RuntimeException("User with Id " + questionRequestDTO.getCreatedById() + " not found")))
                .flatMap(user -> {
                    Question question = QuestionAdapter.toEntity(questionRequestDTO);
                    return questionRepository.save(question)
                            .flatMap(savedQuestion -> {
                                if (savedQuestion.getTagIds() == null || savedQuestion.getTagIds().isEmpty()) {
                                    // No tags case
                                    return questionIndexService.createQuestionIndex(savedQuestion)
                                            .thenReturn(QuestionAdapter.toDTO(savedQuestion, user));
                                }
                                // ✅ All three operations in parallel
                                Mono<Void> indexMono = questionIndexService.createQuestionIndex(savedQuestion);
                                Mono<List<TagResponseDTO>> tagsMono = tagService.findTagsByIds(savedQuestion.getTagIds());
                                Mono<Void> incrementMono = Flux.fromIterable(savedQuestion.getTagIds())
                                        .flatMap(tagService::incrementUsageCount)
                                        .then();

                                return Mono.zip(indexMono.then(Mono.just(1)), tagsMono, incrementMono.then(Mono.just(1)))
                                        .map(tuple -> QuestionAdapter.toDTOWithTagsAndUser(
                                                savedQuestion,
                                                tuple.getT2(),
                                                user
                                        ));
                            });
                })
                .doOnSuccess(response -> {
                        System.out.println("✅ Question created: " + response.getId());
                        QuestionCreatedEvent event = new QuestionCreatedEvent(
                                response.getId(),
                                response.getCreatedByUser() != null ? response.getCreatedByUser().getId() : null,
                                LocalDateTime.now()
                        );
                        kafkaEventProducer.publishQuestionCreatedEvent(event);
                })
                .doOnError(error ->
                        System.err.println("❌ Question creation failed: " + error.getMessage()));
    }

    @Override
    public Mono<QuestionResponseDTO> findQuestionById(String questionId) {
        return questionRepository.findById(questionId)
                .switchIfEmpty(Mono.error(new RuntimeException("Question with Id " + questionId + " not found!")))
                .flatMap(this::enrichQuestionWithTagsAndUser)
                .doOnSuccess(response -> {
                            System.out.println("Question retrieved successfully: " + response);
                            ViewCountEvent viewCountEvent = new ViewCountEvent(questionId,"question",LocalDateTime.now());
                            kafkaEventProducer.publishViewCountEvent(viewCountEvent);
                        }
                )
                .doOnError(error -> System.out.println("Error finding question: " + error));
    }

    @Override
    public Flux<QuestionResponseDTO> findAllQuestions() {
        return this.questionRepository.findAll()
                .flatMap(this::enrichQuestionWithUser)  // ← Now includes user!
                .doOnNext(response -> System.out.println("Questions retrieved successfully: "+ response))
                .doOnError(error -> System.out.println("Error finding all questions: " + error));
    }

    @Override
    public Mono<Void> deleteQuestionById(String questionId) {
        return this.questionRepository.findById(questionId)
                .flatMap(question->this.questionIndexService.deleteQuestionById(question.getId()).thenReturn(question))
                .flatMap(foundQuestion -> {
                    Mono<Void> feedCleanup = userFeedRepository.deleteAllByQuestionId(questionId);
                    if (foundQuestion.getTagIds() != null && !foundQuestion.getTagIds().isEmpty()) {
                        return Flux.fromIterable(foundQuestion.getTagIds())
                                .flatMap(tagService::decrementUsageCount)
                                .then(feedCleanup)
                                .then(this.questionRepository.deleteById(questionId));
                    }
                    return feedCleanup.then(this.questionRepository.deleteById(questionId));
                })
                .doOnSuccess(ignored ->
                        System.out.println("✅ The Question with ID " + questionId + " got deleted successfully"))
                .doOnError(error ->
                        System.err.println("❌ Error while deleting question " + questionId + ": " + error.getMessage()));
    }


    @Override
    public Flux<QuestionResponseDTO> searchQuestions(String searchTerm, Integer offset, Integer pageSize) {
        return questionRepository.findByTitleOrContentContainingIgnoreCase(searchTerm, PageRequest.of(offset,pageSize))
                .flatMap(this::enrichQuestionWithTagsAndUser)
                .doOnError(error -> System.out.println("Error finding questions: " + error))
                .doOnComplete(() -> System.out.println("All questions retrieved successfully"));
    }

    @Override
    public Flux<QuestionResponseDTO> searchQuestionsByCursor(String cursor, int size) {
        // what we should check is if we again want a certain number of records then we should use the pageable object
        // but we don't want the offset then we can keep the offset as 0.
        // if we want the size the we can use again the pageable object.

        // now we have to check if they have passed the cursor to us or not , for that we have to create a cursor util.
        //now the most important thing is now the ordering should be done using createdAt.
        Pageable pageable = PageRequest.of(0, size);

        Flux<Question> questionsFlux = !CursorUtils.isValidCursor(cursor)
                ? questionRepository.findTop10ByOrderByCreatedAtAsc(pageable)
                : questionRepository.findByCreatedAtGreaterThanOrderByCreatedAtAsc(
                CursorUtils.parseCursor(cursor), pageable);

        return questionsFlux
                .flatMap(this::enrichQuestionWithUser)  // ← Add user enrichment
                .doOnError(error -> System.out.println("Error finding questions: " + error))
                .doOnComplete(() -> System.out.println("All questions retrieved successfully"));
    }

    @Override
    public Flux<QuestionResponseDTO> findQuestionsByTags(List<String> tagIds, TagFilterType filterType, int page, int size) {
        if(tagIds == null || tagIds.isEmpty())return Flux.empty();
        Pageable pageable = PageRequest.of(page,size);

        //choose the appropriate repository method based on filter type
        Flux<Question> questionsFlux=switch(filterType){
            case SINGLE->  questionRepository.findByTagId(tagIds.getFirst(),pageable);
            case ANY -> questionRepository.findByTagIdIn(tagIds,pageable);
            case ALL -> questionRepository.findByTagIdAll(tagIds,pageable);
        };

        return questionsFlux
                .flatMap(this::enrichQuestionWithTagsAndUser)
                .doOnNext(response -> System.out.println("Question by tags retrieved successfully: " + response))
                .doOnError(error -> System.out.println("Error finding questions by tags: " + error))
                .doOnComplete(() -> System.out.println("All questions by tags retrieved successfully"));

    }

    // Full enrichment - user and tags
    public Mono<QuestionResponseDTO> enrichQuestionWithTagsAndUser(Question question) {
        if(question.getCreatedById() == null || question.getCreatedById().isEmpty()) {
            return Mono.just(QuestionAdapter.toDTO(question));
        }

        Mono<UserResponseDTO> userMono = userService.findUserById(question.getCreatedById())
                .defaultIfEmpty(UserResponseDTO.builder().build());  // Fallback

        if(question.getTagIds() == null || question.getTagIds().isEmpty()) {
            return userMono.map(user -> QuestionAdapter.toDTO(question, user));
        }

        Mono<List<TagResponseDTO>> tagsMono = tagService.findTagsByIds(question.getTagIds());

        return Mono.zip(userMono, tagsMono)
                .map(tuple -> QuestionAdapter.toDTOWithTagsAndUser(
                        question,
                        tuple.getT2(),  // tags
                        tuple.getT1()   // user
                ));
    }

    public Mono<QuestionResponseDTO> enrichQuestionWithUser(Question question){
        if(question.getCreatedById()==null || question.getCreatedById().isEmpty()){
            return Mono.just(QuestionAdapter.toDTO(question));
        }

        return userService.findUserById(question.getCreatedById())
                .map(user-> QuestionAdapter.toDTO(question,user))
                .defaultIfEmpty(QuestionAdapter.toDTO(question));
    }

    @Override
    public Flux<QuestionResponseDTO> findQuestionsByTagId(String tagId,int page,int size){
       return findQuestionsByTags(List.of(tagId),TagFilterType.SINGLE,page,size);
    }

    @Override
    public Flux<QuestionResponseDTO> findQuestionsByAnyTags(List<String> tagIds, int page, int size) {
        return findQuestionsByTags(tagIds, TagFilterType.ANY, page, size);
    }

    @Override
    public Flux<QuestionResponseDTO> findQuestionsByAllTags(List<String> tagIds, int page, int size) {
        return findQuestionsByTags(tagIds, TagFilterType.ALL, page, size);
    }

    @Override
    public Flux<QuestionElasticDocument> searchQuestionsByElasticSearch(String query){
        return questionIndexService.searchQuestionsByElasticSearch(query);
    }


    public Mono<Void> syncElasticSearchData() {
        return questionIndexService.deleteAllQuestions()
                .thenMany(questionRepository.findAll())  // Use thenMany since findAll() returns Flux
                .flatMap(questionIndexService::createQuestionIndex)  // This returns Mono<Void> for each
                .then()  // Collect all Mono<Void> into a single Mono<Void>
                .doOnSuccess(v -> System.out.println("All question indexes synced successfully"))
                .doOnError(error -> System.err.println("Error syncing indexes: " + error.getMessage()));
    }
}
