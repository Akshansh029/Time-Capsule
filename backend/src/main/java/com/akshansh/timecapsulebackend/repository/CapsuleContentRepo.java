package com.akshansh.timecapsulebackend.repository;

import com.akshansh.timecapsulebackend.model.dto.CapsuleContentDto;
import com.akshansh.timecapsulebackend.model.entity.CapsuleContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CapsuleContentRepo extends JpaRepository<CapsuleContent, UUID> {
    @Query("""
        SELECT new com.akshansh.timecapsulebackend.model.dto.CapsuleContentDto(
            c.id, c.type, c.body, c.fileUrl, c.addedBy.name, c.addedAt
            ) FROM CapsuleContent c WHERE c.capsule.id = :capsuleId
    """)
    Page<CapsuleContentDto> findAllContent(Pageable pageable, @Param("capsuleId") UUID capsuleId);
}
