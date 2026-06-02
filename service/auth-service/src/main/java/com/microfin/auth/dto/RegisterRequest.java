package com.microfin.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * New-user registration payload.
 */
public record RegisterRequest(

        @NotBlank(message = "username is required")
        @Size(min = 3, max = 100)
        String username,

        @NotBlank @Email
        String email,

        @NotBlank
        @Size(min = 8, max = 100, message = "password must be 8-100 characters")
        String password,

        String firstName,

        String lastName) {
}
