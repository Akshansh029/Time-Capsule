package com.akshansh.timecapsulebackend.model.dto;

import com.akshansh.timecapsulebackend.model.entity.MemberRole;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CapsuleMemberDto {
    private UUID id;
    private String name;
    private String email;
}
