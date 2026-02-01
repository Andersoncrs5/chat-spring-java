package com.chat.api.configs.parameters;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "spring.security")
@Validated
public record JwtParameter(
        Exp exp,
        Jwt jwt,
        Refresh refresh,
        Header header,
        Revoke revoke,
        Jti jti
) {

    public record Exp(
            @NotNull @Min(1)
            Integer token,

            @NotNull @Min(1)
            Integer refresh
    ) {}

    public record Jwt(
            @NotBlank
            String secret,

            @NotBlank
            String algorithm,

            @NotBlank
            String issuer,

            @NotBlank
            String audience,

            @NotNull
            Integer clockSkewSeconds
    ) {}

    public record Refresh(
            boolean reuse,
            boolean rotate,

            @Min(1)
            Integer maxUsage
    ) {}

    public record Header(
            @NotBlank
            String name,

            @NotBlank
            String prefix
    ) {}

    public record Revoke(
            boolean enabled
    ) {}

    public record Jti(
            boolean enabled
    ) {}
}