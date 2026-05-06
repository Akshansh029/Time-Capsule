package com.akshansh.timecapsulebackend.exception;

public class InvalidVerificationCode extends RuntimeException {
    public InvalidVerificationCode(String message) {
        super(message);
    }
}
