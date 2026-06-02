package com.microfin.auth.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Standardized API error payload.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        Map<String, Object> details) {
}
