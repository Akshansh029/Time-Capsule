package com.akshansh.timecapsulebackend.service;

import com.akshansh.timecapsulebackend.exception.InvalidVerificationCode;
import com.akshansh.timecapsulebackend.exception.ResourceNotFoundException;
import com.akshansh.timecapsulebackend.exception.UserAlreadyExistsException;
import com.akshansh.timecapsulebackend.mapper.UserMapper;
import com.akshansh.timecapsulebackend.model.dto.*;
import com.akshansh.timecapsulebackend.model.entity.User;
import com.akshansh.timecapsulebackend.model.entity.UserPrincipal;
import com.akshansh.timecapsulebackend.model.entity.UserVerification;
import com.akshansh.timecapsulebackend.repository.UserRepository;
import com.akshansh.timecapsulebackend.repository.UserVerificationRepository;
import com.akshansh.timecapsulebackend.util.JwtUtil;
import com.akshansh.timecapsulebackend.util.VerificationCodeGenerator;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserVerificationRepository verificationRepository;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public boolean checkEmail(String email) {

        if(userRepo.existsByEmail(email)){
            throw new UserAlreadyExistsException(
                    "User with email: " + email + " already exists");
        }

        String code = verificationCodeGenerator.generateVerificationCode();

        UserVerification userVerification = UserVerification.builder()
                .email(email)
                .verificationCode(code)
                .expiresAt(LocalDateTime.now().plusMinutes(2))
                .build();

        // Save verification token
        verificationRepository.save(userVerification);

        // Send email verification code to user
        emailService.sendVerificationEmail(email, code);
        return true;
    }

    public TokenResponse loginUser(@Valid LoginRequest request) {
        // Authenticate email and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal userDetails = (UserPrincipal) userDetailsService.loadUserByUsername(request.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        return new TokenResponse("Login successful", accessToken, refreshToken);
    }

    public TokenResponse refreshToken(String refreshToken) {
        UUID userId = jwtUtil.generateUserIdFromToken(refreshToken);  //refresh token is valid
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new JwtException("Invalid token"));

        UserPrincipal userDetails = (UserPrincipal) userDetailsService.loadUserByUsername(user.getEmail());

        String accessToken = jwtUtil.generateAccessToken(userDetails);

        return new TokenResponse("Token refreshed", accessToken, refreshToken);
    }

    public TokenResponse registerAndVerify(RegisterUserRequest request) {
        UserVerification userVerification = verificationRepository.findByEmail(request.getEmail());

        if (userVerification != null
                && userVerification.getVerificationCode().equals(request.getVerificationCode())
                && userVerification.getExpiresAt().isAfter(LocalDateTime.now())
        ) {
            // Delete all verification codes for the requested email when verified
            List<UserVerification> userVerificationList = verificationRepository.findAllByEmail(request.getEmail());
            verificationRepository.deleteAll(userVerificationList);

            User newUser = new User(
                    request.getName(),
                    request.getEmail(),
                    passwordEncoder.encode(request.getPassword()),
                    LocalDateTime.now()
            );

            userRepo.save(newUser);

            // Authenticate email and password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserPrincipal userDetails = (UserPrincipal) userDetailsService.loadUserByUsername(request.getEmail());
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            return new TokenResponse("User registered successfully", accessToken, refreshToken);
        }
        throw new InvalidVerificationCode("Invalid verification code! Try again");
    }
}
