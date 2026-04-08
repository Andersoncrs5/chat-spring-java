package com.chat.api.utils.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseHttp<T>(
        T data,
        @NotBlank String message,
        List<String> errors,
        @NotBlank String traceId,
        @NotNull Integer version,
        @NotNull boolean status,
        @NotNull OffsetDateTime timestamp
) {
        private static final Integer API_VERSION = 1;

        public static <T> ResponseHttp<T> success(T data, String message) {
                return new ResponseHttp<>(
                        data, message, null,
                        UUID.randomUUID().toString(), API_VERSION, true, OffsetDateTime.now()
                );
        }

        public static <T> ResponseHttp<T> error(String message, List<String> errors) {
                return new ResponseHttp<>(
                        null, message, errors,
                        UUID.randomUUID().toString(), API_VERSION, false, OffsetDateTime.now()
                );
        }

        public static <T> ResponseHttp<T> error(String message, String singleError) {
                return error(message, List.of(singleError));
        }
}