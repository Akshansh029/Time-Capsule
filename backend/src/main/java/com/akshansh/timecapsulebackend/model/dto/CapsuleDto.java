package com.akshansh.timecapsulebackend.model.dto;

import com.akshansh.timecapsulebackend.model.entity.CapsuleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class CapsuleDto {
    private UUID id;
    private String title;
    private CapsuleStatus status;
    private LocalDateTime unlockDate;
    private boolean isPrivate;
//    private String description;
//    private LocalDateTime createdAt;
}
