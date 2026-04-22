package com.akshansh.timecapsulebackend.repository;

import com.akshansh.timecapsulebackend.model.entity.CapsuleContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CapsuleContentRepo extends JpaRepository<CapsuleContent, UUID> {
}
