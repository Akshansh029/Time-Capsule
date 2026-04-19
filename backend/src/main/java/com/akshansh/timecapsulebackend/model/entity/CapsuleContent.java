package com.akshansh.timecapsulebackend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CapsuleContent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capsule_id", nullable = false)
    private Capsule capsule;

    @NotNull(message = "Content type is required")
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType type;

    @Column(name = "body", nullable = true)
    private String body;

    @Column(name = "file_url", nullable = true)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User addedBy;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;
}
