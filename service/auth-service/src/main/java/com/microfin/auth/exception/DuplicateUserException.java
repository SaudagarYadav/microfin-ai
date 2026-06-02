package com.microfin.auth.exception;

/**
 * Thrown when a user attempts to register with an already-used identifier.
 */
public class DuplicateUserException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateUserException(String message) {
        super(message);
    }
}
