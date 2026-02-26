package com.example.QuoraReactiveApp.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCreatedEvent {

    private String questionId;
    private String authorId;
    private LocalDateTime timestamp;

}
