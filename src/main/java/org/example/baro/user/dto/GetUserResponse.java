package org.example.baro.user.dto;

import org.example.baro.user.entity.UserRole;

public record GetUserResponse (
        String email,
        UserRole role
) {
}
