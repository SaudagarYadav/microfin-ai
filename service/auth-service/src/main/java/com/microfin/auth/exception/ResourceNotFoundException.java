package com.microfin.auth.exception;

/**
 * Thrown when a referenced resource cannot be found.
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
