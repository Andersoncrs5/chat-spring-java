package com.chat.api.modules.auth.dto;

import com.chat.api.utils.annotation.global.emailConstraint.EmailConstraint;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @EmailConstraint
        String email,
        @Size(min = 8, max = 50)
        String password
) {
}
