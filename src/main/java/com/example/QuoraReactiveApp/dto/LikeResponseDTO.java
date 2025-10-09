package com.example.QuoraReactiveApp.dto;


import com.example.QuoraReactiveApp.models.Type.LikeType;
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
    private UserResponseDTO user;
    private LocalDateTime createdDate;

}
