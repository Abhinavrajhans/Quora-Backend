package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.adapter.TagAdapter;
import com.example.QuoraReactiveApp.dto.TagRequestDTO;
import com.example.QuoraReactiveApp.dto.TagResponseDTO;
import com.example.QuoraReactiveApp.models.Tag;
import com.example.QuoraReactiveApp.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TagService implements ITagService {

    private final TagRepository tagRepository;

    @Override
    public Mono<TagResponseDTO> createTag(TagRequestDTO tagRequestDTO) {
        return tagRepository.findByName(tagRequestDTO.getName())
                .flatMap(existingTag ->
                        Mono.<TagResponseDTO>error(new RuntimeException(
                                "Tag with name " + tagRequestDTO.getName() + " already exists"))
                )
                .switchIfEmpty(
                    tagRepository
                    .save(TagAdapter.toEntity(tagRequestDTO))
                    .map(TagAdapter::toDTO)
                )
                .doOnSuccess(response -> System.out.println("Successfully created Tag: " + response))
                .doOnError(error -> System.out.println("Error creating Tag: " + error));
    }

    @Override
    public Mono<TagResponseDTO> getTagById(String id)
    {
        return tagRepository.findById(id)
                .map(TagAdapter::toDTO)
                .doOnSuccess(response-> System.out.println(" Successfully retrieved Tag: "+ response))
                .doOnError(error-> System.out.println(" Error getting Tag: "+ error));
    }


    @Override
    public Mono<TagResponseDTO> findTagByName(String name)
    {
        return tagRepository.findByName(name)
                .map(TagAdapter::toDTO)
                .doOnSuccess(response-> System.out.println(" Successfully retrieved Tag: "+ response))
                .doOnError(error-> System.out.println(" Error getting Tag: "+ error));
    }


    @Override
    public Mono<TagResponseDTO> incrementUsageCount(String tagId) {
        return tagRepository.findById(tagId)
                .flatMap(tag -> {
                    tag.setUsageCount(tag.getUsageCount() + 1);
                    return tagRepository.save(tag);
                })
                .map(TagAdapter::toDTO);
    }

    public Mono<TagResponseDTO> decrementUsageCount(String tagId){
        return tagRepository.findById(tagId)
                .flatMap(tag->{
                    tag.setUsageCount(Math.max(tag.getUsageCount() - 1,0));
                    return tagRepository.save(tag);
                })
                .map(TagAdapter::toDTO);
    }
}
