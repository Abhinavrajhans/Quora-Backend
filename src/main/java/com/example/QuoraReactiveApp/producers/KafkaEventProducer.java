package com.example.QuoraReactiveApp.producers;

import com.example.QuoraReactiveApp.config.KafkaConfig;
import com.example.QuoraReactiveApp.events.QuestionCreatedEvent;
import com.example.QuoraReactiveApp.events.ViewCountEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishViewCountEvent(ViewCountEvent viewCountEvent) {
        kafkaTemplate.send(KafkaConfig.TOPIC_NAME, viewCountEvent.getTargetId(), viewCountEvent)
                .whenComplete((result, err) -> {
                    if(err != null) {
                        System.out.println("Error publishing view count event: " + err.getMessage());
                    }
                });
    }

    public void publishQuestionCreatedEvent(QuestionCreatedEvent event) {
        kafkaTemplate.send(KafkaConfig.TOPIC_QUESTION_CREATED, event.getQuestionId(), event)
                .whenComplete((result, err) -> {
                    if (err != null) {
                        System.out.println("Error publishing question created event: " + err.getMessage());
                    }
                });
    }
}
