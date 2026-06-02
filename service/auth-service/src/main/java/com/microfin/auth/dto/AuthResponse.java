package com.microfin.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Authentication success response containing access + refresh tokens.
 *
 * <p>The {@code user} field is included on login/register flows where the
 * full profile is useful to the client, and omitted (null) on refresh
 * flows where the client already has the user context.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(
        String tokenType,
        String accessToken,
        String refreshToken,
        long expiresInSeconds,
        UserResponse user) {
}
