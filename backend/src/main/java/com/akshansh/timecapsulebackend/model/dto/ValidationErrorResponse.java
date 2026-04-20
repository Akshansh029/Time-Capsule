package com.akshansh.timecapsulebackend.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ValidationErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private List<String> errors;

    public ValidationErrorResponse(int status, String error, String message, List<String> errors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.errors = errors;
    }
}