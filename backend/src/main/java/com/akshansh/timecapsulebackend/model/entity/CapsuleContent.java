package com.akshansh.timecapsulebackend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Table(name = "capsule_contents")
public class CapsuleContent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capsule_id", nullable = false)
    @ToString.Exclude
    private Capsule capsule;

    @NotNull(message = "Content type is required")
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::content_type")
    private ContentType type;

    @Column(name = "body", nullable = true)
    private String body;

    @Column(name = "file_url", nullable = true)
    private String fileUrl;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User addedBy;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;
}
