package com.example.springsecuritydemoservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    @NotBlank(message = "Username name should not be empty")
    private String username;

    @NotBlank(message = "Password should not be empty")
    @Size(min = 3, message = "Password should be greater than 3")
    private String password;
}
