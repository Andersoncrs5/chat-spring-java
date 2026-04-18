package com.chat.api.modules.contacts.dto;

import java.time.Instant;
import java.util.UUID;

public record ContactDTO(
        UUID id,
        UUID ownerId,
        UUID contactId,
        String nickname,
        Instant createdAt,
        Instant updatedAt
) {
}
