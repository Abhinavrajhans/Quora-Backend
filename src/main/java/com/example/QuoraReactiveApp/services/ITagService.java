package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.dto.TagRequestDTO;
import com.example.QuoraReactiveApp.dto.TagResponseDTO;
import reactor.core.publisher.Mono;

public interface ITagService {
    public Mono<TagResponseDTO> createTag(TagRequestDTO tagRequestDTO);
    public Mono<TagResponseDTO> getTagById(String id);
    public Mono<TagResponseDTO> findTagByName(String name);
    public Mono<TagResponseDTO> incrementUsageCount(String tagId);

}
