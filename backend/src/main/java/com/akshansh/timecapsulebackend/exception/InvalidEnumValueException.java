package com.akshansh.timecapsulebackend.exception;

public class InvalidEnumValueException extends IllegalArgumentException {
    public InvalidEnumValueException(String message) {
        super(message);
    }
}
