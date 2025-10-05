package com.example.QuoraReactiveApp.controllers;


import com.example.QuoraReactiveApp.dto.TagRequestDTO;
import com.example.QuoraReactiveApp.dto.TagResponseDTO;
import com.example.QuoraReactiveApp.services.ITagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/tags")
@RequiredArgsConstructor
public class TagController {
    
    private final ITagService tagservice;
    
    @PostMapping
    public Mono<TagResponseDTO> createTag(@Valid @RequestBody TagRequestDTO tagRequestDTO)
    {
        return  tagservice.createTag(tagRequestDTO)
                .doOnSuccess(response-> System.out.println(" Successfully Tag Created : "+ response))
                .doOnError(error-> System.out.println(" Error finding Tag: "+ error));
    }


    @GetMapping("/{id}")
    public Mono<TagResponseDTO> findTagById(@PathVariable String id)
    {
        return  tagservice.findTagById(id)
                .doOnSuccess(response-> System.out.println(" Successfully Tag Created : "+ response))
                .doOnError(error-> System.out.println(" Error finding Tag: "+ error));
    }



    @GetMapping("/name/{name}")
    public Flux<TagResponseDTO> findTagByName(@PathVariable String name)
    {
        return  tagservice.findTagByName(name)
                .doOnNext(response-> System.out.println(" Successfully Tag Fetched : "+ response))
                .doOnError(error-> System.out.println(" Error finding Tag: "+ error))
                .doOnComplete(()-> System.out.println("Fetched All the tags Containing :"+name));
    }

    @GetMapping
    public Flux<TagResponseDTO> findAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return tagservice.findAllTags(page, size)
                .doOnNext(response -> System.out.println("Successfully retrieved Tags: " + response))
                .doOnError(error -> System.out.println("Error finding Tags: " + error));
    }


}
