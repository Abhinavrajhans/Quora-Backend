package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.adapter.UserAdapter;
import com.example.QuoraReactiveApp.dto.QuestionResponseDTO;
import com.example.QuoraReactiveApp.dto.UserRequestDTO;
import com.example.QuoraReactiveApp.dto.UserResponseDTO;
import com.example.QuoraReactiveApp.models.User;
import com.example.QuoraReactiveApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final UserRepository userRepository;

    @Override
    public Mono<UserResponseDTO> createUser(UserRequestDTO userRequestDTO)
    {
        User user= UserAdapter.toEntity(userRequestDTO);
        return userRepository.save(user)
                .map(UserAdapter::toDTO)
                .doOnNext(response -> System.out.println("User created Successfully" + response))
                .doOnError(throwable -> System.out.println("User created Failed" + throwable));
    }

    @Override
    public Mono<UserResponseDTO> findUserById(String id)
    {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("User with Id " + id + " not found!")))
                .map(UserAdapter::toDTO)
                .doOnNext(response -> System.out.println("User found Successfully" + response))
                .doOnError(throwable -> System.out.println("User found Failed" + throwable));

    }

    @Override
    public Flux<UserResponseDTO> findAllUsers(int page, int size){
        Pageable pageable =  PageRequest.of(page, size);
        return userRepository.findUserBy(pageable)
                .map(UserAdapter::toDTO)
                .doOnNext(response -> System.out.println("Fetched the User Successfully: " + response))
                .doOnError(error -> System.out.println("Error finding User: " + error))
                .doOnComplete(() -> System.out.println("Fetched All the Users Successfully"));
    }

    @Override
    public Mono<UserResponseDTO> incrementFollowerCount(String id)
    {
        return userRepository.findById(id)
                .flatMap(user-> {
                    user.setFollowerCount(user.getFollowerCount()+1);
                    return userRepository.save(user);
                })
                .map(UserAdapter::toDTO)
                .doOnSuccess(response -> System.out.println("User follower Count Incremented Successfully" + response))
                .doOnError(throwable -> System.out.println("User follower Count Incrementation Failed" + throwable));

    }

    @Override
    public Mono<UserResponseDTO> incrementFollowingCount(String id)
    {
        return userRepository.findById(id)
                .flatMap(user->{
                    user.setFollowingCount(user.getFollowingCount()+1);
                    return userRepository.save(user);
                })
                .map(UserAdapter::toDTO)
                .doOnSuccess(response -> System.out.println("User following Count Incremented Successfully" + response))
                .doOnError(throwable -> System.out.println("User following Count Incrementation Failed" + throwable));
    }


}
