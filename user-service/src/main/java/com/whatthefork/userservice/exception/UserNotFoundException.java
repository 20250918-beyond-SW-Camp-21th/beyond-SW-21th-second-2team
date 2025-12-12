package com.whatthefork.userservice.exception;

/**
 * Exception thrown when a user cannot be found by ID
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}