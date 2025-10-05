package com.example.QuoraReactiveApp.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {

    @NotBlank(message="username is required")
    @Size(min=2,max=100,message="username must be between 2 and 100 characters")
    private String username;

    @NotBlank(message="email is required")
    @Email(message="Email should be valid")
    private String email;

    @Size(max=500 , message="Bio should less than 500 characters")
    private String bio;
}
