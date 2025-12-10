package com.whatthefork.userservice.exception;

/**
 * Exception thrown when attempting to register a user with an email that already exists
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}