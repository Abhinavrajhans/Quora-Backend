package com.example.QuoraReactiveApp.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TagRequestDTO {

    @NotBlank(message="Tag name is required")
    @Size(min=2,max=50,message="Tag name must be between 2 and 50 characters")
    private String name;

    @Size(max=200 , message = "Description must not exceed 200 characters")
    private String description;

}
