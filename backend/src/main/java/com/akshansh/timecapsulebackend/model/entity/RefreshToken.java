package com.akshansh.timecapsulebackend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_tokens_token_hash", columnList = "token_hash"),
                @Index(name = "idx_refresh_tokens_family_id",  columnList = "family_id"),
                @Index(name = "idx_refresh_tokens_user_id",    columnList = "user_id")
        }
)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * SHA-256 hex digest of the raw refresh token string.
     * The raw token is only ever held in memory and sent to the client — never stored.
     */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    /**
     * The user this refresh token belongs to.
     * Mapped as a plain UUID to avoid loading the full User entity on every auth check.
     * Change to @ManyToOne if you need the User object directly.
     */
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    /**
     * Groups the entire rotation chain that started from one login event.
     * When reuse is detected, every token sharing this familyId is invalidated.
     */
    @Column(name = "family_id", nullable = false, updatable = false)
    private UUID familyId;

    /**
     * Flipped to true when this token is rotated out.
     * If a used token is presented again, it signals theft → invalidate the whole family.
     */
    @Column(name = "used", nullable = false)
    private boolean used = false;

    /** Absolute timestamp after which this token must be rejected regardless of `used`. */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /** Returns true if this token is past its expiry timestamp. */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    /**
     * Returns true if this token is safe to use:
     *   - not yet marked as used
     *   - not past its expiry
     */
    public boolean isValid() {
        return !used && !isExpired();
    }
}
