package com.akshansh.timecapsulebackend.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LockedCapsuleDto extends CapsuleDto {
    private Long daysUntilUnlock;
}
