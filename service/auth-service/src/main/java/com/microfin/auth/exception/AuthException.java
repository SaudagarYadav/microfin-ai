package com.microfin.auth.exception;

/**
 * Thrown when authentication or authorization fails for business reasons.
 */
public class AuthException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
