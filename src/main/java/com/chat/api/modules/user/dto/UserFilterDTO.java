package com.chat.api.modules.user.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserFilterDTO(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String name,
        String username,
        String email,
        String phoneNumber,
        Set<String> roles
) {}