package com.example.springsecuritydemoservice.exception;

import jakarta.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(Exception cause) {
        super(cause);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
