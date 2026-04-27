package com.akshansh.timecapsulebackend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Email(message = "Email must be valid")
    @Size(min = 5, max = 100, message = "Email must be between 5 and 100 characters")
    @NotBlank(message = "Email is required")
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(max = 250, message = "Password can be max of 255 characters")
    @Column(name = "password", nullable = false)
    @ToString.Exclude
    private String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Capsule> ownedCapsules = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "addedBy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<CapsuleContent> ownedContents;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<CapsuleMember> memberList;

    public User(String name, String email, String passwordHash, LocalDateTime now) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = now;
    }
}
