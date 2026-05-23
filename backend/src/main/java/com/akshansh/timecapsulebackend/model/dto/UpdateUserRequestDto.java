package com.akshansh.timecapsulebackend.model.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequestDto {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @Email(message = "Email must be valid")
    @Size(min = 5, max = 100, message = "Email must be between 5 and 100 characters")
    @NotBlank(message = "Email is required")
    private String email;
}
