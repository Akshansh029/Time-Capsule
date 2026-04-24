package com.akshansh.timecapsulebackend.repository;

import com.akshansh.timecapsulebackend.model.dto.CapsuleDto;
import com.akshansh.timecapsulebackend.model.entity.Capsule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CapsuleRepository extends JpaRepository<Capsule, UUID> {

    // Find All Capsules for a user
    @Query("""
        SELECT new com.akshansh.timecapsulebackend.model.dto.CapsuleDto(
        c.id, c.title, c.description, c.status, c.unlockDate, c.isPrivate, c.createdAt, c.owner.id, c.owner.name) FROM Capsule c
        WHERE c.owner.id = :userId
    """)
    Page<CapsuleDto> findAllCapsules(Pageable pageable, UUID userId);

    // Find All Capsules for a user with search
    @Query("""
        SELECT new com.akshansh.timecapsulebackend.model.dto.CapsuleDto(
        c.id, c.title, c.description, c.status, c.unlockDate, c.isPrivate, c.createdAt, c.owner.id, c.owner.name) FROM Capsule c
        WHERE c.owner.id = :userId
        AND (c.title ILIKE CONCAT('%', :search, '%')
        OR c.description ILIKE CONCAT('%', :search, '%'))
""")
    Page<CapsuleDto> findAllCapsulesWithSearch(
            Pageable pageable,
            @Param("userId") UUID userId,
            @Param("search") String search
    );

    Capsule findBySlug(String slug);
}
