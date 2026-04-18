package com.chat.api.modules.contacts.dto;

import java.util.UUID;

public record CreateContactDTO(
        String nickname,
        UUID contactId
) {
}
