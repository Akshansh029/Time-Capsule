package com.akshansh.timecapsulebackend.model.dto;

import com.akshansh.timecapsulebackend.model.entity.CapsuleContent;
import com.akshansh.timecapsulebackend.model.entity.CapsuleMember;
import com.akshansh.timecapsulebackend.model.entity.CapsuleStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnlockedCapsuleDto extends CapsuleDto {
    private List<CapsuleContentDto> contents;
}
