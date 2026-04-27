package com.akshansh.timecapsulebackend.model.dto;

import com.akshansh.timecapsulebackend.model.entity.MemberRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CapsuleMemberDto {
    private String name;
    private String email;
}
