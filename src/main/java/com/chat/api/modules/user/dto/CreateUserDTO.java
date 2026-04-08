package com.chat.api.modules.user.dto;

import jakarta.validation.constraints.Size;

public record CreateUserDTO(
        String name,
        String username,
        String email,
        @Size(min = 8, max = 50)
        String password,
        String bannerUrl,
        String bio,
        String phoneNumber
) {
}
