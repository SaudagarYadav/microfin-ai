package com.microfin.auth.dto;

import java.util.Set;

/**
 * Public, non-sensitive user representation.
 */
public record UserResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        Set<String> roles) {
}
