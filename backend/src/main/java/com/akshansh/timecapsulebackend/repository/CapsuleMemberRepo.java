package com.akshansh.timecapsulebackend.repository;

import com.akshansh.timecapsulebackend.model.entity.CapsuleMember;
import com.akshansh.timecapsulebackend.model.entity.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.ScopedValue;
import java.util.Optional;
import java.util.UUID;

public interface CapsuleMemberRepo extends JpaRepository<CapsuleMember, UUID> {
    boolean existsByCapsuleIdAndUserId(UUID capsuleId, UUID userId);

    boolean existsByCapsuleIdAndUserIdAndRole(UUID capsuleId, UUID userId, MemberRole role);

    Optional<CapsuleMember> findByCapsuleIdAndUserId(UUID id, UUID requesterId);
}
