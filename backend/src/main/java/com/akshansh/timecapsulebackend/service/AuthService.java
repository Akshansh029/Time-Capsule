package com.akshansh.timecapsulebackend.service;

import com.akshansh.timecapsulebackend.model.dto.RegisterUserRequest;
import com.akshansh.timecapsulebackend.model.dto.UserDto;
import com.akshansh.timecapsulebackend.model.entity.User;
import com.akshansh.timecapsulebackend.model.entity.UserPrincipal;
import com.akshansh.timecapsulebackend.repository.UserRepository;
import com.akshansh.timecapsulebackend.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserDto registerUser(@Valid RegisterUserRequest request) {
        User user = userRepo.findByEmail(request.getEmail());

        if(user != null){
            throw new UserAlreadyExistsException(
                    "User with email: " + request.getEmail() + " already exists");
        }

        User newUser = new User(
                request.getName(),
                request.getEmail(),
                UserRole.VIEWER,
                passwordEncoder.encode(request.getPassword()));

        userRepo.save(newUser);
        return convertToDto(newUser);
    }

    public LoginResponse loginUser(@Valid LoginRequest request) {
        // Authenticate email and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal userDetails = (UserPrincipal) userDetailsService.loadUserByUsername(request.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        return new LoginResponse("Login successful", accessToken, refreshToken);
    }

    public LoginResponse refreshToken(String refreshToken) {
        Long userId = jwtUtil.generateUserIdFromToken(refreshToken);  //refresh token is valid
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + userId + " not found"));

        UserPrincipal userDetails = (UserPrincipal) userDetailsService.loadUserByUsername(user.getEmail());

        String accessToken = jwtUtil.generateAccessToken(userDetails);

        return new LoginResponse("Token refreshed", accessToken, refreshToken);
    }
}
