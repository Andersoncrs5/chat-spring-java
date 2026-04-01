package com.chat.api.modules.user.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserDTO(
        UUID id,
        String name,
        String username,
        String email,
        String bannerUrl,
        String phoneNumber,
        Long version,
        Set<String> roles,
        Instant createdAt,
        Instant updatedAt
) {
}
