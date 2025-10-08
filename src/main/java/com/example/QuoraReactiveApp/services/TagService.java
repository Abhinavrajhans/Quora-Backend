package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.adapter.TagAdapter;
import com.example.QuoraReactiveApp.dto.TagRequestDTO;
import com.example.QuoraReactiveApp.dto.TagResponseDTO;
import com.example.QuoraReactiveApp.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService implements ITagService {

    private final TagRepository tagRepository;

    @Override
    public Mono<TagResponseDTO> createTag(TagRequestDTO tagRequestDTO) {
        return tagRepository.findByNameIgnoreCase(tagRequestDTO.getName())
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
    public Mono<TagResponseDTO> findTagById(String id)
    {
        return tagRepository.findById(id)
                .map(TagAdapter::toDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("Tag with Id "+id +" not found")))
                .doOnSuccess(response-> System.out.println(" Successfully retrieved Tag: "+ response))
                .doOnError(error-> System.out.println(" Error finding Tag: "+ error));
    }


    @Override
    public Flux<TagResponseDTO> findTagByName(String name)
    {
        return tagRepository.findByNameContainingIgnoreCase(name)
                .map(TagAdapter::toDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("Tag with name "+name+" not found")))
                .doOnNext(response-> System.out.println("Successfully retrieved Tag: "+ response))
                .doOnError(error -> System.out.println(" Error finding Tag: "+ error))
                .doOnComplete(()-> System.out.println("Fetched All the tags Containing :"+name));
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

    @Override
    public Mono<TagResponseDTO> decrementUsageCount(String tagId){
        return tagRepository.findById(tagId)
                .flatMap(tag->{
                    tag.setUsageCount(Math.max(tag.getUsageCount() - 1,0));
                    return tagRepository.save(tag);
                })
                .map(TagAdapter::toDTO);
    }



    @Override
    public Flux<TagResponseDTO> findAllTags(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tagRepository.findAllBy(pageable)
                .map(TagAdapter::toDTO)
                .doOnNext(response -> System.out.println("Fetched the Tags Successfully: " + response))
                .doOnError(error -> System.out.println("Error Finding Tags: " + error))
                .doOnComplete(() -> System.out.println("Fetched All the tags Successfully"));
    }

    @Override
    public Mono<List<TagResponseDTO>> findTagsByIds(List<String> tagIds)
    {
        if(tagIds==null || tagIds.isEmpty()) return Mono.just(List.of());

        return tagRepository.findAllById(tagIds)
                .map(TagAdapter::toDTO)
                .collectList()
                .doOnSuccess(tags -> System.out.println("✅ Fetched " + tags.size() + " tags in batch"))
                .doOnError(error -> System.err.println("❌ Error fetching tags: " + error.getMessage()));
    }
}
