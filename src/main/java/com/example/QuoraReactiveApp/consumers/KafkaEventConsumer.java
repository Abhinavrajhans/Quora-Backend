package com.example.QuoraReactiveApp.consumers;


import com.example.QuoraReactiveApp.config.KafkaConfig;
import com.example.QuoraReactiveApp.events.ViewCountEvent;
import com.example.QuoraReactiveApp.models.Question;
import com.example.QuoraReactiveApp.repositories.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class KafkaEventConsumer {

    private final QuestionRepository questionRepository;

    //inorder to make sure this method is handled by kafka we have to add the kafka listener
    @KafkaListener(
            topics= KafkaConfig.TOPIC_NAME,
            groupId = "view-count-consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleViewCountEvent(ViewCountEvent event){
        questionRepository.findById(event.getTargetId())
                .flatMap(question-> {
                    System.out.println("Incrementing view count for question:  " + question.getId());
                    question.setViews(question.getViews() + 1);
                    return questionRepository.save(question);
                })
                .subscribe(updatedQuestion ->{
                    System.out.println("View Count incremented for question : "+updatedQuestion.getId());
                },error->{
                    System.out.println("Error while updating question : "+error.getMessage());
                });
    }
}
