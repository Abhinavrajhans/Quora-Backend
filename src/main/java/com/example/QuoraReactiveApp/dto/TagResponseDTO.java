package com.example.QuoraReactiveApp.dto;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TagResponseDTO {
    private String id;
    private String name;
    private String description;
    private Integer usageCount;
    private LocalDateTime createdAt;
}
