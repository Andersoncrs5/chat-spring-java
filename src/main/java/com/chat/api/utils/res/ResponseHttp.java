package com.chat.api.utils.res;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record ResponseHttp<T>(
        T data,

        @NotBlank
        String message,

        @NotBlank
        String traceId,

        @NotNull
        Integer version,

        @NotNull
        boolean status,

        @NotNull
        OffsetDateTime timestamp
) { }

