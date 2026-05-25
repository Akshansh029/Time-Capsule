package com.akshansh.timecapsulebackend.service;

import com.akshansh.timecapsulebackend.exception.ResourceNotFoundException;
import com.akshansh.timecapsulebackend.mapper.UserMapper;
import com.akshansh.timecapsulebackend.model.dto.ActiveUserResponse;
import com.akshansh.timecapsulebackend.model.dto.UpdateUsernameRequestDto;
import com.akshansh.timecapsulebackend.model.dto.UserDto;
import com.akshansh.timecapsulebackend.model.entity.User;
import com.akshansh.timecapsulebackend.model.entity.UserPrincipal;
import com.akshansh.timecapsulebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.akshansh.timecapsulebackend.util.UserUtil.getCurrentUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final String USER_CACHE = "userDetails";

    @Cacheable(
            cacheNames = USER_CACHE,
            key = "T(com.akshansh.timecapsulebackend.util.UserUtil).getCurrentUser().getUserId()"
    )
    public ActiveUserResponse getActiveUserDetails() {
        UserPrincipal currentUser = getCurrentUser();

        log.info("Successfully fetched user details for user: {}", currentUser.getUserId());
        return userRepo.findActiveUserDetails(currentUser.getUserId());
    }

    @Transactional
    @CacheEvict(
            cacheNames = USER_CACHE,
            key = "T(com.akshansh.timecapsulebackend.util.UserUtil).getCurrentUser().getUserId()"
    )
    public UserDto updateUser(UpdateUsernameRequestDto request){
        UUID currentUserId = getCurrentUser().getUserId();
        log.info("Updating user with id: {}", currentUserId);

        User currentUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> {
                    log.error("User with ID: {} not found", currentUserId);
                    return new ResourceNotFoundException("User not found");
                });

        if(Objects.nonNull(request.getName()) && !"".equalsIgnoreCase(request.getName())){
            currentUser.setName(request.getName());
        }

        // Save updated user
        userRepo.save(currentUser);
        log.info("Successfully updated the username with ID: {}", currentUserId);
        return userMapper.toDto(currentUser);
    }

    @Transactional
    @CacheEvict(cacheNames = USER_CACHE,
            key = "T(com.akshansh.timecapsulebackend.util.UserUtil).getCurrentUser().getUserId()"
    )
    public void deleteUser(){
        UUID currentUserId = getCurrentUser().getUserId();
        log.info("Deleting user with id: {}", currentUserId);

        User currentUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> {
                    log.error("User with ID: {} not found", currentUserId);
                    return new ResourceNotFoundException("User not found");
                });

        log.info("Successfully delete user with ID: {}", currentUserId);
        userRepo.delete(currentUser);
    }
}
