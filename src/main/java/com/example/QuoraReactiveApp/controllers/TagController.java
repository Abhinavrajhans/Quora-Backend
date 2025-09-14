package com.example.QuoraReactiveApp.controllers;


import com.example.QuoraReactiveApp.dto.TagRequestDTO;
import com.example.QuoraReactiveApp.dto.TagResponseDTO;
import com.example.QuoraReactiveApp.services.ITagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
                .doOnError(error-> System.out.println(" Error getting Tag: "+ error));
    }


    @GetMapping("/{id}")
    public Mono<TagResponseDTO> getTagById(@PathVariable String id)
    {
        return  tagservice.getTagById(id)
                .doOnSuccess(response-> System.out.println(" Successfully Tag Created : "+ response))
                .doOnError(error-> System.out.println(" Error getting Tag: "+ error));
    }


}
