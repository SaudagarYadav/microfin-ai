package com.microfin.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Login request payload (username or email + password).
 */
public record LoginRequest(

        @NotBlank(message = "username is required")
        String username,

        @NotBlank(message = "password is required")
        String password) {
}
