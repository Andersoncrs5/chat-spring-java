package com.chat.api.modules.contacts.dto;

import java.util.UUID;

public record ContactFilterDTO(
        String nickname,
        UUID contactId
) {}
