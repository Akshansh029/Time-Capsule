package com.akshansh.timecapsulebackend.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class LockedCapsuleDto extends CapsuleDto {
    private Long daysUntilUnlock;
}
