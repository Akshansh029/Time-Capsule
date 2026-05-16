package com.akshansh.timecapsulebackend.mapper;

import com.akshansh.timecapsulebackend.model.dto.*;
import com.akshansh.timecapsulebackend.model.entity.*;
import com.akshansh.timecapsulebackend.service.AesEncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.akshansh.timecapsulebackend.util.SlugUtil.generateSlug;

@Component
@RequiredArgsConstructor
public class CapsuleMapper {

    @Value("${aws.bucket.name}")
    private String bucketName;

    private final S3Presigner s3Presigner;
    private final AesEncryptionService aesEncryptionService;

    private static final Duration PRESIGN_DURATION = Duration.ofMinutes(30);

    public String generatePresignedUrl(String key, Duration expiration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(r ->
                r.signatureDuration(expiration)
                        .getObjectRequest(getObjectRequest));

        return presigned.url().toString();
    }

    public CapsuleDto toDto(Capsule capsule) {
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

    public Capsule toEntity(CreateCapsuleRequest request, User owner) {
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

    public LockedCapsuleDto toLockedCapsuleDto(Capsule capsule) {
        LockedCapsuleDto dto = LockedCapsuleDto.builder().build();
        mapBase(capsule, dto);

        dto.setCapsuleMembers(capsule.getMembers().stream()
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

    public UnlockedCapsuleDto toUnlockedCapsuleDto(Capsule capsule) {
        UnlockedCapsuleDto dto = UnlockedCapsuleDto.builder().build();
        mapBase(capsule, dto);
        dto.setDescription(capsule.getDescription());

        dto.setCapsuleMembers(capsule.getMembers().stream()
                .map(m -> CapsuleMemberDto.builder()
                        .id(m.getUser().getId())
                        .name(m.getUser().getName())
                        .email(m.getUser().getEmail())
                        .build())
                .toList());

        dto.setContents(
                capsule.getContents().stream()
                        .map(this::toContentDto)
                        .toList()
        );
        return dto;
    }

    private void mapBase(Capsule capsule, CapsuleDto dto) {
        dto.setId(capsule.getId());
        dto.setTitle(capsule.getTitle());
        dto.setStatus(capsule.getStatus());
        dto.setUnlockDate(capsule.getUnlockDate());
        dto.setIsPrivate(capsule.isPrivate());
        dto.setCreatedAt(capsule.getCreatedAt());
        dto.setOwnerId(capsule.getOwner().getId());
        dto.setOwnerName(capsule.getOwner().getName());
    }

    private CapsuleContentDto toContentDto(CapsuleContent content) {
        CapsuleContentDto dto = new CapsuleContentDto();

        dto.setId(content.getId());
        dto.setType(content.getType());
        dto.setBody(aesEncryptionService.decrypt(content.getBody(), content.getEncryptionIv()));
        dto.setFileUrl(content.getFileUrl());
        dto.setPreAssignedUrl(content.getFileUrl() != null ? generatePresignedUrl(content.getFileUrl(), PRESIGN_DURATION) : null);
        dto.setAddedByName(content.getAddedBy().getName());
        dto.setAddedAt(content.getAddedAt());
        return dto;
    }
}
