package com.microfin.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.microfin.auth.config.JwtProperties;
import com.microfin.auth.constants.AuthConstants;
import com.microfin.auth.entity.User;

import lombok.RequiredArgsConstructor;

/**
 * Encodes signed JWT access tokens using the shared OAuth2 JWK source (RSA, RS256).
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    /**
     * Issue a signed access token for the given user.
     *
     * @param user authenticated user (must not be {@code null})
     * @return encoded JWT token plus expiry epoch
     */
    public IssuedToken issueAccessToken(final User user) {
        final Instant now = Instant.now();
        final Instant expiresAt = now.plus(jwtProperties.accessTokenTtlMinutes(), ChronoUnit.MINUTES);

        final Set<String> roles = user.getRoles().stream()
                .map(r -> r.getRoleName())
                .collect(Collectors.toUnmodifiableSet());

        final JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getUsername())
                .claim(AuthConstants.CLAIM_USER_ID, user.getId())
                .claim(AuthConstants.CLAIM_ROLES, roles)
                .claim(AuthConstants.CLAIM_TOKEN_TYPE, AuthConstants.TOKEN_TYPE_ACCESS)
                .build();

        final JwsHeader header = JwsHeader.with(() -> "RS256").build();
        final Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));

        return new IssuedToken(jwt.getTokenValue(), expiresAt);
    }

    public long getAccessTokenTtlSeconds() {
        return jwtProperties.accessTokenTtlMinutes() * 60L;
    }

    public record IssuedToken(String token, Instant expiresAt) {
    }
}
