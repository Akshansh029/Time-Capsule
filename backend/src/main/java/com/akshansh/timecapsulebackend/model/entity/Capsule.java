package com.akshansh.timecapsulebackend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Table(name = "capsules")
public class Capsule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(unique = true, nullable = false)
    private String slug;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 75, message = "Title must be between 3 to 75 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 100, message = "Description can be maximum of 1000 characters")
    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "capsule_status")
    @ColumnTransformer(write = "?::capsule_status")
    private CapsuleStatus status;

    @NotNull(message = "Unlock date cannot be null")
    @Column(name = "unlock_date", nullable = false)
    private LocalDateTime unlockDate;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude
    private User owner;

    @JsonIgnore
    @OneToMany(mappedBy = "capsule", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<CapsuleContent> contents = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "capsule", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<CapsuleMember> members = new HashSet<>();

}
