package com.akshansh.timecapsulebackend.model.dto;

import com.akshansh.timecapsulebackend.model.entity.CapsuleStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateCapsuleRequest {
    @Size(min = 3, max = 75, message = "Title must be between 3 to 75 characters")
    private String title;

    @Size(max = 100, message = "Description can be maximum of 1000 characters")
    private String description;

    private CapsuleStatus status;

    private LocalDateTime unlockDate;

    private Boolean isPrivate;
}
