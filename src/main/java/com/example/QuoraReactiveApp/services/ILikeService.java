package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.dto.AnswerRequestDTO;
import com.example.QuoraReactiveApp.dto.AnswerResponseDTO;
import com.example.QuoraReactiveApp.dto.LikeRequestDTO;
import com.example.QuoraReactiveApp.dto.LikeResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ILikeService {
    public Mono<LikeResponseDTO> createLike(LikeRequestDTO like);
    public Mono<LikeResponseDTO> findLikeById(String id);
    public Mono<LikeResponseDTO> countLikeByTargetAndTargetTypeAndUserID(String targetId, String targetType);
    public Mono<LikeResponseDTO> countDisLikeByTargetAndTargetTypeAndUserID(String targetId, String targetType);
    public Mono<LikeResponseDTO> toggleLike(String targetId, String targetType , Boolean isLike);
    public Mono<LikeResponseDTO> findLikeByTargetIdAndTargetType(String targetId, String targetType);
}
