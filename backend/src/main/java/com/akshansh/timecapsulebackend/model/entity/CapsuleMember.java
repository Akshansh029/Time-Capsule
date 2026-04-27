package com.akshansh.timecapsulebackend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Table(name = "capsule_members")
public class CapsuleMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capsule_id", nullable = false)
    @ToString.Exclude
    private Capsule capsule;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @ColumnTransformer(write = "?::member_role")
    private MemberRole role;
}
