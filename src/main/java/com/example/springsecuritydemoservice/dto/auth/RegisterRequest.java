package com.example.springsecuritydemoservice.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message = "First name should not be empty")
    private String firstName;

    @NotBlank(message = "Last name should not be empty")
    private String lastName;

    @NotBlank(message = "Username name should not be empty")
    private String username;

    @NotBlank(message = "Password should not be empty")
    @Size(min = 3, message = "Password should be greater than 3")
    private String password;
}
