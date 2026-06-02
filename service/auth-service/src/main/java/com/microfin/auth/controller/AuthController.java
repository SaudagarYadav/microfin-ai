package com.microfin.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microfin.auth.constants.AuthConstants;
import com.microfin.auth.dto.AuthResponse;
import com.microfin.auth.dto.LoginRequest;
import com.microfin.auth.dto.RefreshTokenRequest;
import com.microfin.auth.dto.RegisterRequest;
import com.microfin.auth.dto.UserResponse;
import com.microfin.auth.exception.ResourceNotFoundException;
import com.microfin.auth.repository.UserRepository;
import com.microfin.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Authentication REST endpoints (registration, login, token refresh, logout, profile).
 */
@RestController
@RequestMapping(AuthConstants.AUTH_BASE)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody final RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody final LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody final RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody(required = false) final RefreshTokenRequest request) {
        authService.logout(request == null ? null : request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    /**
     * Exchanges the current authentication (e.g. HTTP Basic) for a JWT access token
     * and refresh token. Useful for clients that want to authenticate once with
     * username/password and then use Bearer tokens for subsequent calls.
     */
    @PostMapping("/token")
    public ResponseEntity<AuthResponse> token(final Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authService.issueTokensForUsername(authentication.getName()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(final Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Works for both JWT (sub claim) and HTTP Basic (UserDetails username)
        final String username = authentication.getName();
        return userRepository.findByUsername(username)
                .map(authService::toUserResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
}
