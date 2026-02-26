package com.example.QuoraReactiveApp.consumers;


import com.example.QuoraReactiveApp.config.KafkaConfig;
import com.example.QuoraReactiveApp.events.QuestionCreatedEvent;
import com.example.QuoraReactiveApp.events.ViewCountEvent;
import com.example.QuoraReactiveApp.models.UserFeed;
import com.example.QuoraReactiveApp.repositories.FollowRepository;
import com.example.QuoraReactiveApp.repositories.QuestionRepository;
import com.example.QuoraReactiveApp.repositories.UserFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventConsumer {

    private final QuestionRepository questionRepository;
    private final FollowRepository followRepository;
    private final UserFeedRepository userFeedRepository;

    @KafkaListener(
            topics= KafkaConfig.TOPIC_NAME,
            groupId = "view-count-consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleViewCountEvent(ViewCountEvent event){
        questionRepository.findById(event.getTargetId())
                .flatMap(question-> {
                    System.out.println("Incrementing view count for question:  " + question.getId());
                    Integer view=question.getViews() ;
                    question.setViews( view == null ? 0 : view + 1);
                    return questionRepository.save(question);
                })
                .subscribe(updatedQuestion ->{
                    System.out.println("View Count incremented for question : "+updatedQuestion.getId());
                },error->{
                    System.out.println("Error while updating question : "+error.getMessage());
                });
    }

    @KafkaListener(
            topics = KafkaConfig.TOPIC_QUESTION_CREATED,
            groupId = "feed-consumer",
            containerFactory = "feedKafkaListenerContainerFactory"
    )
    public void handleQuestionCreatedEvent(QuestionCreatedEvent event) {
        System.out.println("Building feed for question: " + event.getQuestionId() + " by author: " + event.getAuthorId());

        followRepository.findByFollowingId(event.getAuthorId())
                .flatMap(follow -> {
                    UserFeed feedItem = UserFeed.builder()
                            .userId(follow.getFollowerId())
                            .questionId(event.getQuestionId())
                            .authorId(event.getAuthorId())
                            .questionCreatedAt(event.getTimestamp())
                            .build();
                    return userFeedRepository.save(feedItem);
                })
                .subscribe(
                        feedItem -> System.out.println("Feed item created for user: " + feedItem.getUserId()),
                        error -> System.err.println("Error building feed for question " + event.getQuestionId() + ": " + error.getMessage()),
                        () -> System.out.println("Feed population completed for question: " + event.getQuestionId())
                );
    }
}
