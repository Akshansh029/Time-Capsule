package com.akshansh.timecapsulebackend.model.dto;

import com.akshansh.timecapsulebackend.model.entity.MemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddMemberRequestDto {
    @NotNull
    private UUID capsuleId;
    @NotBlank
    private String userEmail;
    @NotNull
    private MemberRole role;
}
