package com.akshansh.timecapsulebackend.service;

import com.akshansh.timecapsulebackend.exception.CapsuleAlreadyUnlockedException;
import com.akshansh.timecapsulebackend.exception.ResourceNotFoundException;
import com.akshansh.timecapsulebackend.exception.UnlockDatePassedException;
import com.akshansh.timecapsulebackend.mapper.CapsuleMapper;
import com.akshansh.timecapsulebackend.model.dto.*;
import com.akshansh.timecapsulebackend.model.entity.*;
import com.akshansh.timecapsulebackend.repository.CapsuleContentRepo;
import com.akshansh.timecapsulebackend.repository.CapsuleMemberRepo;
import com.akshansh.timecapsulebackend.repository.CapsuleRepository;
import com.akshansh.timecapsulebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.akshansh.timecapsulebackend.util.UserUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
public class CapsuleService {

    private final CapsuleRepository capsuleRepo;
    private final UserRepository userRepo;
    private final CapsuleMemberRepo capsuleMemberRepo;
    private final CapsuleContentRepo capsuleContentRepo;

    private boolean isOwner(Capsule capsule, UUID currentUserId){
        return capsule.getOwner().getId().equals(currentUserId);
    }

    private boolean isMember(UUID capsuleId, UUID currentUserId){
        return capsuleMemberRepo
                .existsByCapsuleIdAndUserId(capsuleId, currentUserId);
    }

    private boolean isContributor(UUID capsuleId, UUID currentUserId){
        return capsuleMemberRepo
                .existsByCapsuleIdAndUserIdAndRole(capsuleId, currentUserId, MemberRole.CONTRIBUTOR);
    }

    private boolean isContentAuthor(CapsuleContent content, UUID currentUserId){
        return content.getAddedBy().getId().equals(currentUserId);
    }

    @Transactional
    public CapsuleDto createCapsule(CreateCapsuleRequest request){
        UUID currentUserId = getCurrentUser().getUserId();

        if(request.getUnlockDate().isBefore(LocalDateTime.now())){
            throw new UnlockDatePassedException("Invalid unlock date");
        }

        User currentUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Save new capsule
        Capsule newCapsule = CapsuleMapper.toEntity(request, currentUser);
        capsuleRepo.save(newCapsule);

        // Save the contents
        if(request.getContents() != null && !request.getContents().isEmpty()){
            List<CapsuleContent> contents = request.getContents().stream()
                    .map(c -> CapsuleContent.builder()
                            .capsule(newCapsule)
                            .type(c.getType())
                            .body(c.getBody())
                            .fileUrl(c.getFileUrl())
                            .addedBy(userRepo.getReferenceById(currentUserId))
                            .addedAt(LocalDateTime.now())
                            .build())
                    .toList();
            capsuleContentRepo.saveAll(contents);
        }

        // Save the capsule members
        if(!request.getIsPrivate() && request.getMembers() != null && !request.getMembers().isEmpty()){
            for(AddMemberRequestDto m : request.getMembers()){
                User invitee = userRepo.findByEmail(m.getUserEmail());

                if(invitee == null){
                    throw new ResourceNotFoundException("Invitee not found: " + m.getUserEmail());
                }

                capsuleMemberRepo.save(CapsuleMember.builder()
                        .capsule(newCapsule)
                        .user(invitee)
                        .role(m.getRole())
                        .build());
            }
        }

        return CapsuleMapper.toDto(newCapsule);
    }

    public Page<CapsuleDto> getAllCapsulesForUser(int pageNo, int pageSize, String search){
        UUID currentUserId = getCurrentUser().getUserId();
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        if(search == null || search.isBlank()){
            return capsuleRepo.findAllCapsules(pageable, currentUserId);
        }

        return capsuleRepo.findAllCapsulesWithSearch(pageable, currentUserId, search);
    }

    public CapsuleDto getCapsuleDetails(String slug) {
        UUID currentUserId = getCurrentUser().getUserId();

        Capsule capsule = capsuleRepo.findBySlug(slug);

        if(capsule == null){
            throw new ResourceNotFoundException("Capsule not found");
        }

        // Private capsules are only visible to owner + members
        if (capsule.isPrivate() && !isOwner(capsule, currentUserId) && !isMember(capsule.getId(), currentUserId)) {
            throw new AccessDeniedException("You do not have access to this capsule");
        }

        // Return based on status (not unlock date)
        if (capsule.getStatus() == CapsuleStatus.UNLOCKED) {
            return CapsuleMapper.toUnlockedCapsuleDto(capsule);
        }

        return CapsuleMapper.toLockedCapsuleDto(capsule);
    }

    @Transactional
    public CapsuleDto updateCapsule(UpdateCapsuleRequest request, String slug){
        UUID currentUserId = getCurrentUser().getUserId();

        Capsule capsule = capsuleRepo.findBySlug(slug);

        if(capsule == null){
            throw new ResourceNotFoundException("Capsule not found");
        }

        if (!isOwner(capsule, currentUserId)) {
            throw new AccessDeniedException("You do not have access to this capsule");
        }
        if (capsule.getStatus() == CapsuleStatus.UNLOCKED) {
            throw new CapsuleAlreadyUnlockedException("Capsule already unlocked");
        }

        // Updates fields if they are not null or empty.
        if (Objects.nonNull(request.getTitle()) && !"".equalsIgnoreCase(request.getTitle())) {
            capsule.setTitle(request.getTitle());
        }
        if (Objects.nonNull(request.getDescription()) && !"".equalsIgnoreCase(request.getDescription())) {
            capsule.setDescription(request.getDescription());
        }
        if (Objects.nonNull(request.getStatus())) {
            capsule.setStatus(request.getStatus());
        }
        if (Objects.nonNull(request.getUnlockDate())) {
            capsule.setUnlockDate(request.getUnlockDate());
        }
        if (Objects.nonNull(request.getIsPrivate())) {
            capsule.setPrivate(request.getIsPrivate());
        }

        // Save the updated capsule
        capsuleRepo.save(capsule);
        return CapsuleMapper.toDto(capsule);
    }

    @Transactional
    public void deleteCapsule(String slug){
        UUID currentUserId = getCurrentUser().getUserId();

        Capsule capsule = capsuleRepo.findBySlug(slug);

        if(capsule == null){
            throw new ResourceNotFoundException("Capsule not found");
        }

        if (!isOwner(capsule, currentUserId)) {
            throw new AccessDeniedException("You do not have access to this capsule");
        }

        capsuleRepo.deleteBySlug(slug);
    }


    @Transactional
    public void addContentsToCapsule(List<AddContentRequestDto> request, String slug){
        UUID currentUserId = getCurrentUser().getUserId();

        User requester = userRepo.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Capsule capsule = capsuleRepo.findBySlug(slug);

        if(capsule == null){
            throw new ResourceNotFoundException("Capsule not found");
        }

        // Can't add content to an already unlocked capsule
        if (capsule.getStatus() == CapsuleStatus.UNLOCKED) {
            throw new IllegalStateException("Cannot add content to an unlocked capsule");
        }

        // Only owner or contributors can add content
        if (!isOwner(capsule, currentUserId) && !isContributor(capsule.getId(), currentUserId)) {
            throw new AccessDeniedException("You do not have permission to add content");
        }

        List<CapsuleContent> contents = new ArrayList<>();
        for(AddContentRequestDto contentDto : request){
            CapsuleContent content = new CapsuleContent();

            content.setCapsule(capsule);
            content.setAddedBy(requester);
            content.setType(contentDto.getType());
            content.setBody(contentDto.getBody());
            content.setFileUrl(contentDto.getFileUrl());
            content.setAddedAt(LocalDateTime.now());

            capsuleContentRepo.save(content);
            contents.add(content);
        }

        // save content and capsule
        capsule.setContents(contents);
        capsuleRepo.save(capsule);
    }

    @Transactional
    public void removeContent(String slug, UUID contentId) {
        UUID currentUserId = getCurrentUser().getUserId();

        Capsule capsule = capsuleRepo.findBySlug(slug);

        if(capsule == null){
            throw new ResourceNotFoundException("Capsule not found");
        }

        CapsuleContent content = capsuleContentRepo.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        // Content must belong to this capsule
        if (!content.getCapsule().getId().equals(capsule.getId())) {
            throw new ResourceNotFoundException("Content not found in this capsule");
        }

        if (content.getCapsule().getStatus() == CapsuleStatus.UNLOCKED) {
            throw new CapsuleAlreadyUnlockedException("Cannot remove content from an unlocked capsule");
        }

        // Only the person who added it OR the capsule owner can delete
        if (!isOwner(content.getCapsule(), currentUserId) && !isContentAuthor(content, currentUserId)) {
            throw new AccessDeniedException("You do not have permission to remove this content");
        }

        capsuleContentRepo.delete(content);
    }

    public Page<CapsuleContentDto> getAllContentsForCapsule(String slug, int pageNo, int pageSize){
        Capsule capsule = capsuleRepo.findBySlug(slug);

        if(capsule == null){
            throw new ResourceNotFoundException("Capsule not found");
        }

        if (capsule.getStatus() == CapsuleStatus.LOCKED) {
            throw new CapsuleAlreadyUnlockedException("Capsule is locked");
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return capsuleContentRepo.findAllContent(pageable, capsule.getId());
    }
}
