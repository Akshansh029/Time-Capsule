package com.akshansh.timecapsulebackend.service;

import com.akshansh.timecapsulebackend.exception.ResourceNotFoundException;
import com.akshansh.timecapsulebackend.mapper.CapsuleMapper;
import com.akshansh.timecapsulebackend.model.dto.CapsuleDto;
import com.akshansh.timecapsulebackend.model.dto.CreateCapsuleRequest;
import com.akshansh.timecapsulebackend.model.dto.UnlockedCapsuleDto;
import com.akshansh.timecapsulebackend.model.entity.Capsule;
import com.akshansh.timecapsulebackend.model.entity.CapsuleStatus;
import com.akshansh.timecapsulebackend.model.entity.User;
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
import java.util.UUID;

import static com.akshansh.timecapsulebackend.util.UserUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
public class CapsuleService {

    private final CapsuleRepository capsuleRepo;
    private final UserRepository userRepo;
    private final CapsuleMemberRepo capsuleMemberRepo;

    @Transactional
    public CapsuleDto createCapsule(CreateCapsuleRequest request){
        UUID currentUserId = getCurrentUser().getUserId();

        User currentUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Capsule newCapsule = CapsuleMapper.toEntity(request, currentUser);

        capsuleRepo.save(newCapsule);
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

    public CapsuleDto getCapsuleDetails(UUID capsuleId) {
        UUID currentUserId = getCurrentUser().getUserId();

        Capsule capsule = capsuleRepo.findById(capsuleId)
                .orElseThrow(() -> new ResourceNotFoundException("Capsule not found"));

        // Private capsules are only visible to owner + members
        boolean isOwner = capsule.getOwner().getId().equals(currentUserId);

        boolean isMember = capsuleMemberRepo
                .existsByCapsuleIdAndUserId(capsuleId, currentUserId);

        if (capsule.isPrivate() && !isOwner && !isMember) {
            throw new AccessDeniedException("You do not have access to this capsule");
        }

        // --- Return based on status (not unlock date) ---
        if (capsule.getStatus() == CapsuleStatus.UNLOCKED) {
            return CapsuleMapper.toUnlockedCapsuleDto(capsule);
        }

        return CapsuleMapper.toLockedCapsuleDto(capsule);
    }
}
