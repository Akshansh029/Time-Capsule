package com.akshansh.timecapsulebackend.repository;

import com.akshansh.timecapsulebackend.model.dto.ActiveUserResponse;
import com.akshansh.timecapsulebackend.model.dto.UserDto;
import com.akshansh.timecapsulebackend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);

    // Fetch active user details
    @Query("SELECT new com.akshansh.timecapsulebackend.model.dto.ActiveUserResponse(" +
            "u.id, u.name, u.email, u.createdAt) " +
            "FROM User u WHERE u.id = :userId")
    ActiveUserResponse findActiveUserDetails(UUID userId);

    List<UserDto> findByNameContainingOrEmailContaining(String name, String email);
}
