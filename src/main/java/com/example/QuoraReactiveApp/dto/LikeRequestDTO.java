package com.example.QuoraReactiveApp.dto;

import com.example.QuoraReactiveApp.models.LikeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeRequestDTO {

    @NotBlank(message="Target Id is required")
    private String targetId;

    @NotNull(message="Like Type is required")
    private LikeType likeType;

    @NotNull(message = "Is Like is required")
    private Boolean isLike;
}
