package com.akshansh.timecapsulebackend.repository;

import com.akshansh.timecapsulebackend.model.entity.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserVerificationRepository extends JpaRepository<UserVerification, UUID> {
    Optional<UserVerification> findFirstByEmailOrderByExpiresAtDesc(String email);

    UserVerification findByEmail(String email);

    List<UserVerification> findAllByEmail(String email);

    List<UserVerification> findAllByExpiresAtBefore(Instant expiresAtBefore);
}
