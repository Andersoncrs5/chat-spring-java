package com.chat.api.modules.user.dto;

public record UpdateUserDTO(
        String name,
        String username,
        String password,
        String bannerUrl,
        String phoneNumber
) {
}
