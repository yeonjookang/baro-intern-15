package org.example.baro.user.dto;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest (
        @NotBlank String email,
        @NotBlank String password
) {
}
