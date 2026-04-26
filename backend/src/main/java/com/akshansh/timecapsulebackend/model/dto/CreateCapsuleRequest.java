package com.akshansh.timecapsulebackend.model.dto;

import com.akshansh.timecapsulebackend.model.entity.CapsuleContent;
import com.akshansh.timecapsulebackend.model.entity.CapsuleMember;
import com.akshansh.timecapsulebackend.model.entity.CapsuleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class CreateCapsuleRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 75, message = "Title must be between 3 to 75 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 100, message = "Description can be maximum of 1000 characters")
    private String description;

    @NotNull(message = "Unlock date cannot be null")
    private LocalDateTime unlockDate;

    @NotNull(message = "Public/Private should be specified")
    private Boolean isPrivate;

    @Size(max = 10)
    private List<AddContentRequestDto> contents;

    private Set<AddMemberRequestDto> members;
}
