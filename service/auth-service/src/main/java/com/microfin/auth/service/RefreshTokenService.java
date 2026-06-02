package com.microfin.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microfin.auth.config.JwtProperties;
import com.microfin.auth.entity.RefreshToken;
import com.microfin.auth.entity.User;
import com.microfin.auth.exception.AuthException;
import com.microfin.auth.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

/**
 * Manages persistent, opaque refresh tokens with rotation and revocation.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshTokenService.class);

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public RefreshToken create(final User user) {
        final Instant expiresAt = Instant.now().plus(jwtProperties.refreshTokenTtlDays(), ChronoUnit.DAYS);
        final RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString() + "." + UUID.randomUUID())
                .user(user)
                .issuedAt(Instant.now())
                .expiryDate(expiresAt)
                .revoked(Boolean.FALSE)
                .build();
        return refreshTokenRepository.save(token);
    }

    @Transactional(readOnly = true)
    public RefreshToken validate(final String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            throw new AuthException("Refresh token is required");
        }
        final RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new AuthException("Refresh token not found"));
        if (!token.isActive()) {
            LOGGER.warn("Refresh token rejected for userId={} (revoked={}, expired={})",
                    token.getUser().getId(), token.getRevoked(), token.isExpired());
            throw new AuthException("Refresh token is invalid or expired");
        }
        return token;
    }

    @Transactional
    public RefreshToken rotate(final RefreshToken existing) {
        existing.setRevoked(Boolean.TRUE);
        refreshTokenRepository.save(existing);
        return create(existing.getUser());
    }

    @Transactional
    public void revoke(final String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            return;
        }
        final int count = refreshTokenRepository.revokeByToken(tokenValue);
        LOGGER.debug("Revoked {} refresh token(s)", count);
    }

    @Transactional
    public void revokeAllForUser(final User user) {
        final int count = refreshTokenRepository.revokeAllByUser(user);
        LOGGER.info("Revoked {} refresh token(s) for userId={}", count, user.getId());
    }
}
