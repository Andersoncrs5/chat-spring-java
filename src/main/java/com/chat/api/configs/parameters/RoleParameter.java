package com.chat.api.configs.parameters;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import java.util.List;

@ConfigurationProperties(prefix = "config")
@Validated
public record RoleParameter(
        Role role
) {
    public record Role(
            @NotEmpty
            List<String> roles
    ) {}
}