package com.example.QuoraReactiveApp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "user_feeds")
public class UserFeed {

    @Id
    private String id;

    @Indexed
    private String userId;              // the follower who should see this in their feed

    @Indexed
    private String questionId;          // the question to show

    private String authorId;            // who posted the question

    private LocalDateTime questionCreatedAt; // original question creation time, used for ordering

    @CreatedDate
    private LocalDateTime addedAt;      // when this item was added to the feed
}
