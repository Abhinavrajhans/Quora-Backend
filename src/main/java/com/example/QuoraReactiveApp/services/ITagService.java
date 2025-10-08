package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.dto.TagRequestDTO;
import com.example.QuoraReactiveApp.dto.TagResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ITagService {
    public Mono<TagResponseDTO> createTag(TagRequestDTO tagRequestDTO);
    public Mono<TagResponseDTO> findTagById(String id);
    public Flux<TagResponseDTO> findTagByName(String name);
    public Mono<TagResponseDTO> incrementUsageCount(String tagId);
    public Mono<TagResponseDTO> decrementUsageCount(String tagId);
    public Flux<TagResponseDTO> findAllTags(int page,int size);
    public Mono<List<TagResponseDTO>> findTagsByIds(List<String> tagIds);
}
