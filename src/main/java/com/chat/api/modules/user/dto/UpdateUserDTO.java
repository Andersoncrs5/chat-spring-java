package com.chat.api.modules.user.dto;

public record UpdateUserDTO(
        String name,
        String username,
        String email,
        String bannerUrl,
        String phoneNumber
) {
}
