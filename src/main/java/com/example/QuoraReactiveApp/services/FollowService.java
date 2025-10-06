package com.example.QuoraReactiveApp.services;


import com.example.QuoraReactiveApp.adapter.FollowAdapter;
import com.example.QuoraReactiveApp.dto.FollowRequestDTO;
import com.example.QuoraReactiveApp.dto.FollowResponseDTO;
import com.example.QuoraReactiveApp.dto.UserResponseDTO;
import com.example.QuoraReactiveApp.models.Follow;
import com.example.QuoraReactiveApp.repositories.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FollowService implements IFollowService{

    private final FollowRepository followRepository;
    private final UserService userService;


    @Override
    public Mono<FollowResponseDTO> createFollow(FollowRequestDTO followRequestDTO)
    {
            Follow follow= FollowAdapter.toEntity(followRequestDTO);
            return followRepository.save(follow)
                    .flatMap( savedFollow->{
                            Mono<UserResponseDTO> followedUser =userService.incrementFollowerCount(follow.getFollowingId());
                            Mono<UserResponseDTO> follower =userService.incrementFollowingCount(follow.getFollowerId());
                            return Mono.zip(followedUser,follower)
                                    .thenReturn(savedFollow);
                    })
                    .map(FollowAdapter::toDTO)
                    .doOnSuccess(response -> System.out.println("Follow created successfully: "+ response))
                    .doOnError(throwable -> System.out.println("Follow creation failed: "+ throwable.getMessage()));
    }

    @Override
    public Mono<FollowResponseDTO> findFollowById(String id)
    {
        return followRepository.findById(id)
                .map(FollowAdapter::toDTO)
                .doOnSuccess(response -> System.out.println("Follow found successfully: "+ response))
                .doOnError(throwable -> System.out.println("Follow found failed: "+ throwable.getMessage()));
    }

    @Override
    public Flux<FollowResponseDTO> findAllFollowersOfUserId(String userId)
    {
        return followRepository.findByFollowingId(userId)
                    .doOnNext(response-> System.out.println("Succesfully got the follower "+response))
                    .doOnError(error-> System.out.println("Error in getting the follower :" +error))
                    .doOnComplete(()-> System.out.println("Successfully fetched all the follwers"));
    }

    @Override
    public Flux<FollowResponseDTO> findAllFollowingOfUserId(String userId)
    {
        return followRepository.findByFollowerId(userId)
                .doOnNext(response-> System.out.println("Succesfully got the follower "+response))
                .doOnError(error-> System.out.println("Error in getting the follower :" +error))
                .doOnComplete(()-> System.out.println("Successfully fetched all the follwers"));
    }

}
