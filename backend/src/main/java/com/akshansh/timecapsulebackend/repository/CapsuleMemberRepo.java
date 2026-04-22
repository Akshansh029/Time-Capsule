package com.akshansh.timecapsulebackend.repository;

import com.akshansh.timecapsulebackend.model.entity.CapsuleMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CapsuleMemberRepo extends JpaRepository<CapsuleMember, UUID> {
    boolean existsByCapsuleIdAndUserId(UUID capsuleId, UUID userId);
}
