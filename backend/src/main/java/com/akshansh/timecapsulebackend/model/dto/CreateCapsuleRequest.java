package com.akshansh.timecapsulebackend.model.dto;

import com.akshansh.timecapsulebackend.model.entity.CapsuleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateCapsuleRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 75, message = "Title must be between 3 to 75 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 100, message = "Description can be maximum of 1000 characters")
    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CapsuleStatus status;

    @NotNull(message = "Unlock date cannot be null")
    @Column(name = "unlock_date", nullable = false)
    private LocalDateTime unlockDate;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;
}
