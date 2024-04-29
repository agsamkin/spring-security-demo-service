package com.example.springsecuritydemoservice.exception.custom;

public class WrongPasswordException extends IllegalStateException {
    public WrongPasswordException(Exception cause) {
        super(cause);
    }

    public WrongPasswordException(String message) {
        super(message);
    }
}
