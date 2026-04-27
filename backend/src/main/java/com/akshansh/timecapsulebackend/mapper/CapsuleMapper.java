package com.akshansh.timecapsulebackend.mapper;

import com.akshansh.timecapsulebackend.model.dto.*;
import com.akshansh.timecapsulebackend.model.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.akshansh.timecapsulebackend.util.SlugUtil.generateSlug;

@Component
public class CapsuleMapper {

    public static CapsuleDto toDto(Capsule capsule){
        return CapsuleDto.builder()
                .id(capsule.getId())
                .slug(capsule.getSlug())
                .title(capsule.getTitle())
                .status(capsule.getStatus())
                .unlockDate(capsule.getUnlockDate())
                .isPrivate(capsule.isPrivate())
                .createdAt(capsule.getCreatedAt())
                .ownerId(capsule.getOwner().getId())
                .ownerName(capsule.getOwner().getName())
                .build();
    }

    public static Capsule toEntity(CreateCapsuleRequest request, User owner){
        return Capsule.builder()
                .slug(generateSlug(request.getTitle()))
                .title(request.getTitle())
                .description(request.getDescription())
                .status(CapsuleStatus.LOCKED)
                .unlockDate(request.getUnlockDate())
                .isPrivate(request.getIsPrivate())
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static LockedCapsuleDto toLockedCapsuleDto(Capsule capsule) {
        LockedCapsuleDto dto = LockedCapsuleDto.builder().build();
        mapBase(capsule, dto);

        List<CapsuleMember> membersCopy = new ArrayList<>(capsule.getMembers());

        dto.setCapsuleMembers(membersCopy.stream()
                .map(m -> CapsuleMemberDto.builder()
                        .id(m.getUser().getId())
                        .name(m.getUser().getName())
                        .email(m.getUser().getEmail())
                        .build())
                .toList());

        dto.setDescription(capsule.getDescription());
        dto.setDaysUntilUnlock(
                ChronoUnit.DAYS.between(LocalDateTime.now(), capsule.getUnlockDate())
        );
        return dto;
    }

    public static UnlockedCapsuleDto toUnlockedCapsuleDto(Capsule capsule) {
        UnlockedCapsuleDto dto = UnlockedCapsuleDto.builder().build();
        mapBase(capsule, dto);
        dto.setDescription(capsule.getDescription());

        List<CapsuleMember> membersCopy = new ArrayList<>(capsule.getMembers());

        dto.setCapsuleMembers(membersCopy.stream()
                .map(m -> CapsuleMemberDto.builder()
                        .id(m.getUser().getId())
                        .name(m.getUser().getName())
                        .email(m.getUser().getEmail())
                        .build())
                .toList());
        dto.setContents(
                capsule.getContents().stream()
                        .map(CapsuleMapper::toContentDto)
                        .toList()
        );
        return dto;
    }

    // Shared base mapping — single place to update if fields change
    private static void mapBase(Capsule capsule, CapsuleDto dto) {
        dto.setId(capsule.getId());
        dto.setTitle(capsule.getTitle());
        dto.setStatus(capsule.getStatus());
        dto.setUnlockDate(capsule.getUnlockDate());
        dto.setIsPrivate(capsule.isPrivate());
        dto.setCreatedAt(capsule.getCreatedAt());
        dto.setOwnerId(capsule.getOwner().getId());
        dto.setOwnerName(capsule.getOwner().getName());
    }

    private static CapsuleContentDto toContentDto(CapsuleContent content) {
        CapsuleContentDto dto = new CapsuleContentDto();
        dto.setId(content.getId());
        dto.setType(content.getType());
        dto.setBody(content.getBody());
        dto.setFileUrl(content.getFileUrl());
        dto.setAddedByName(content.getAddedBy().getName());
        dto.setAddedAt(content.getAddedAt());
        return dto;
    }
}
