package com.akshansh.timecapsulebackend.exception;

public class CapsuleAlreadyUnlockedException extends RuntimeException {
    public CapsuleAlreadyUnlockedException(String message) {
        super(message);
    }
}
