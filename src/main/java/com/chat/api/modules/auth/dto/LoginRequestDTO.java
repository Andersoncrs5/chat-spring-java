package com.chat.api.modules.auth.dto;

import com.chat.api.utils.annotation.global.emailConstraint.EmailConstraint;

public record LoginRequestDTO(
        @EmailConstraint
        String email,
        String password
) {
}
