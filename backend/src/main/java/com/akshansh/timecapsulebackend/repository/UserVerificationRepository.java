package com.akshansh.timecapsulebackend.repository;

import com.akshansh.timecapsulebackend.model.entity.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserVerificationRepository extends JpaRepository<UserVerification, UUID> {
    UserVerification findByEmail(String email);
}
