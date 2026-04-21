package com.akshansh.timecapsulebackend.service;

import com.akshansh.timecapsulebackend.model.dto.ActiveUserResponse;
import com.akshansh.timecapsulebackend.model.entity.UserPrincipal;
import com.akshansh.timecapsulebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.akshansh.timecapsulebackend.util.UserUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    public ActiveUserResponse getActiveUserDetails() {
        UserPrincipal currentUser = getCurrentUser();

        return userRepo.findActiveUserDetails(currentUser.getUserId());
    }
}
