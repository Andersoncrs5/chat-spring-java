package com.chat.api.utils.res;

import com.chat.api.modules.user.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record ResponseToken(
        @NotBlank
        String token,

        @NotBlank
        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_at")
        Instant tokenExp,

        @JsonProperty("refresh_expires_at")
        Instant refreshTokenExp,

        UserDTO user
) {
    public ResponseToken {
        if (tokenType == null) tokenType = "Bearer";
    }
}