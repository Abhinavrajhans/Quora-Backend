package com.example.QuoraReactiveApp.dto;


import com.example.QuoraReactiveApp.models.LikeType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponseDTO {

    private String id;
    private String targetId;
    private LikeType likeType;
    private Boolean isLike;
    private LocalDateTime createdDate;

}
