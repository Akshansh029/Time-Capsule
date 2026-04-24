package com.akshansh.timecapsulebackend.model.dto;

import com.akshansh.timecapsulebackend.model.entity.CapsuleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CapsuleDto {
    private UUID id;
    private String title;
    private String description;
    private CapsuleStatus status;
    private LocalDateTime unlockDate;
    private boolean isPrivate;
    private LocalDateTime createdAt;
    private UUID ownerId;
    private String ownerName;
}
