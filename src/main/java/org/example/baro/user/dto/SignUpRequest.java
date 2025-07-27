package org.example.baro.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.baro.user.entity.UserRole;

public record SignUpRequest (
    @NotBlank
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    String email,
    @NotBlank
    String password,
    @NotNull
    UserRole role
) {
}
