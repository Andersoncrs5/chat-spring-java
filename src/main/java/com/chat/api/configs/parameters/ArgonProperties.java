package com.chat.api.configs.parameters;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.argon")
public record ArgonProperties(
        @NotNull
        int saltLength,
        @NotNull
        int hashLength,
        @NotNull
        int parallelism,
        @NotNull
        int memory,
        @NotNull
        int iterations
) {}
