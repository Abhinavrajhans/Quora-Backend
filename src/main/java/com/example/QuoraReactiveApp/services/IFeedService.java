package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import reactor.core.publisher.Flux;

public interface IFeedService {

    Flux<QuestionResponseDTO> getFeedForUser(String userId, int page, int size);
}
