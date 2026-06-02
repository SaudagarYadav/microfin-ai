package com.microfin.auth.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Strongly-typed JWT configuration bound to {@code app.security.jwt.*}.
 */
@Validated
@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(

        @NotBlank
        String issuer,

        @Min(1)
        long accessTokenTtlMinutes,

        @Min(1)
        long refreshTokenTtlDays) {
}
