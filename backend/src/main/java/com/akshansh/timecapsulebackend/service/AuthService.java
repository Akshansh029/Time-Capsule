package com.akshansh.timecapsulebackend.service;

import com.akshansh.timecapsulebackend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
}
