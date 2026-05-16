package com.akshansh.timecapsulebackend.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnlockedCapsuleDto extends CapsuleDto {
    private String description;
    private List<CapsuleMemberDto> capsuleMembers;
    private List<CapsuleContentDto> contents;
}
