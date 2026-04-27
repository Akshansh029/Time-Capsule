package com.akshansh.timecapsulebackend.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LockedCapsuleDto extends CapsuleDto {
    private String description;
    private Long daysUntilUnlock;
    private List<CapsuleMemberDto> capsuleMembers;
}
