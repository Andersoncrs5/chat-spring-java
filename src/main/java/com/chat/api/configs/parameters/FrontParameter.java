package com.chat.api.configs.parameters;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties(prefix = "config")
@Validated
public record FrontParameter(
        Front front
) {
    public record Front(
            @NotBlank
            String url,

            @NotNull
            List<String> methods,

            @NotNull
            List<String> allowedHeaders,

            @NotNull
            Long maxAge,

            boolean allowCredentials
    ) {}
}