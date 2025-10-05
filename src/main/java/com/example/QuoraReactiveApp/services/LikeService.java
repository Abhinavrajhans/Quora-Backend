package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.adapter.LikeAdapter;
import com.example.QuoraReactiveApp.dto.LikeRequestDTO;
import com.example.QuoraReactiveApp.dto.LikeResponseDTO;
import com.example.QuoraReactiveApp.models.Like;
import com.example.QuoraReactiveApp.repositories.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LikeService implements ILikeService{

    private final LikeRepository likeRepository;

    @Override
    public Mono<LikeResponseDTO> createLike(LikeRequestDTO like) {
        Like likeEntity= LikeAdapter.toEntity(like);
        return likeRepository.save(likeEntity)
                .map(LikeAdapter::toDTO)
                .doOnSuccess(response -> System.out.println("Like is Created Successfully: "+ response))
                .doOnError(error -> System.out.println("Like creation Failed: "+ error));
    }

    @Override
    public Mono<LikeResponseDTO> findLikeById(String id) {
        return likeRepository.findById(id)
                .map(LikeAdapter::toDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("Like with Id "+id +" not found")))
                .doOnSuccess(response -> System.out.println("Like is Found Successfully: "+ response))
                .doOnError(error -> System.out.println("Like find Failed: "+ error));
    }

    @Override
    public Mono<LikeResponseDTO> countLikeByTargetAndTargetTypeAndUserID(String targetId, String targetType) {
        return null;
    }

    @Override
    public Mono<LikeResponseDTO> countDisLikeByTargetAndTargetTypeAndUserID(String targetId, String targetType) {
        return null;
    }

    @Override
    public Mono<LikeResponseDTO> toggleLike(String targetId, String targetType, Boolean isLike) {
        return null;
    }

    @Override
    public Mono<LikeResponseDTO> findLikeByTargetIdAndTargetType(String targetId, String targetType) {
        return null;
    }
}
