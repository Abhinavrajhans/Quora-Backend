package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import com.example.QuoraReactiveApp.repositories.QuestionRepository;
import com.example.QuoraReactiveApp.repositories.UserFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class FeedService implements IFeedService {

    private final UserFeedRepository userFeedRepository;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;

    @Override
    public Flux<QuestionResponseDTO> getFeedForUser(String userId, int page, int size) {
        return userFeedRepository.findByUserIdOrderByQuestionCreatedAtDesc(userId, PageRequest.of(page, size))
                .flatMap(feedItem -> questionRepository.findById(feedItem.getQuestionId()))
                // questions deleted after being added to feed are silently skipped (Mono.empty())
                .flatMap(questionService::enrichQuestionWithTagsAndUser)
                .doOnNext(q -> System.out.println("Feed item served for user " + userId + ": question " + q.getId()))
                .doOnError(e -> System.err.println("Error retrieving feed for user " + userId + ": " + e.getMessage()))
                .doOnComplete(() -> System.out.println("Feed retrieval completed for user: " + userId));
    }
}
