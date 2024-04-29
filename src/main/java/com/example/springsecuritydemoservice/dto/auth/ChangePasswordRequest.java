package com.example.springsecuritydemoservice.dto.auth;

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
public class ChangePasswordRequest {
    @NotBlank(message = "Current password should not be empty")
    private String currentPassword;

    @NotBlank(message = "New password should not be empty")
    @Size(min = 3, message = "New password should be greater than 3")
    private String newPassword;

    @NotBlank(message = "Confirmation password should not be empty")
    @Size(min = 3, message = "Confirmation password should be greater than 3")
    private String confirmationPassword;
}
