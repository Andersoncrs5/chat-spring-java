package com.chat.api.modules.user.dto;

public record CreateUserDTO(
        String name,
        String username,
        String email,
        String password,
        String bannerUrl,
        String bio,
        String phoneNumber
) {
}
