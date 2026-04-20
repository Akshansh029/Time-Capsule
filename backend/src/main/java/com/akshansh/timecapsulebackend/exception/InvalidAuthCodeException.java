package com.akshansh.timecapsulebackend.exception;

public class InvalidAuthCodeException extends RuntimeException {
    public InvalidAuthCodeException(String message) {
        super(message);
    }
}
