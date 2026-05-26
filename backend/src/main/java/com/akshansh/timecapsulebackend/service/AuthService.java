package com.akshansh.timecapsulebackend.service;

import com.akshansh.timecapsulebackend.exception.InvalidVerificationCode;
import com.akshansh.timecapsulebackend.exception.ResourceNotFoundException;
import com.akshansh.timecapsulebackend.exception.UserAlreadyExistsException;
import com.akshansh.timecapsulebackend.model.dto.*;
import com.akshansh.timecapsulebackend.model.entity.RefreshToken;
import com.akshansh.timecapsulebackend.model.entity.User;
import com.akshansh.timecapsulebackend.model.entity.UserPrincipal;
import com.akshansh.timecapsulebackend.model.entity.UserVerification;
import com.akshansh.timecapsulebackend.repository.RefreshTokenRepository;
import com.akshansh.timecapsulebackend.repository.UserRepository;
import com.akshansh.timecapsulebackend.repository.UserVerificationRepository;
import com.akshansh.timecapsulebackend.util.JwtUtil;
import com.akshansh.timecapsulebackend.util.VerificationCodeGenerator;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserVerificationRepository verificationRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final ResendEmailService resendEmailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public boolean checkEmail(String email) {
        log.info("Checking user mail: userMail={}", email);
        if(userRepo.existsByEmail(email)){
            throw new UserAlreadyExistsException(
                    "User with email: " + email + " already exists");
        }

        String code = verificationCodeGenerator.generateVerificationCode();

        UserVerification userVerification = UserVerification.builder()
                .email(email)
                .verificationCode(code)
                .expiresAt(Instant.now().plus(2, ChronoUnit.MINUTES))
                .build();

        // Save verification token
        verificationRepo.save(userVerification);

        // Send email verification code to user
        resendEmailService.sendVerificationEmail(email, code);
        return true;
    }

    @Transactional
    public TokenResponse loginUser(@Valid LoginRequest request) {
        log.info("Attempting login: userMail={}", request.getEmail());
        // Authenticate email and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal userDetails = (UserPrincipal) userDetailsService.loadUserByUsername(request.getEmail());

        log.info("Login successful: userId={}", userDetails.getUserId());
        // Issue token and return response
        return issueTokens(userDetails, "Login successful");
    }

    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        log.info("Refreshing token");
        // Hash the raw refresh token
        String tokenHash = jwtUtil.hashToken(refreshToken);

        RefreshToken stored = refreshTokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(() -> new JwtException("Invalid refresh token"));

        User user = userRepo.findById(stored.getUserId())
                .orElseThrow(() -> new JwtException("Invalid refresh token"));

        // Reuse detected
        if (!stored.isValid()) {
            if (stored.isUsed()) {
                log.warn("Refresh token reuse detected: userId={}", stored.getUserId());
                refreshTokenRepo.deleteByFamilyId(stored.getFamilyId()); // nuke family
            }
            throw new JwtException("Invalid refresh token");
        }

        // Mark current token as used
        stored.setUsed(true);
        refreshTokenRepo.save(stored);

        // Issue new refresh token of same family
        String newRefreshToken = UUID.randomUUID().toString();
        RefreshToken newToken = RefreshToken.builder()
                .tokenHash(jwtUtil.hashToken(newRefreshToken))
                .userId(stored.getUserId())
                .familyId(stored.getFamilyId())
                .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();
        refreshTokenRepo.save(newToken);

        UserPrincipal userDetails = (UserPrincipal) userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateAccessToken(userDetails);

        log.info("Token refreshed successfully: userId={}", stored.getUserId());
        return new TokenResponse("Token refreshed", accessToken, newRefreshToken);
    }

    @Transactional
    public TokenResponse registerAndVerify(RegisterUserRequest request) {
        log.info("Registering user: userMail={}", request.getEmail());
        // Fetch the latest verification code for the requested mail
        UserVerification userVerification = verificationRepo
                .findFirstByEmailOrderByExpiresAtDesc(request.getEmail())
                .orElseThrow(() -> new InvalidVerificationCode("Invalid verification code! Try again"));

        // Check if code is valid
        if (userVerification.getVerificationCode().equals(request.getVerificationCode())
                && userVerification.getExpiresAt().isAfter(Instant.now())
        ) {
            // Delete all verification codes for the requested email when verified
            List<UserVerification> userVerificationList = verificationRepo.findAllByEmail(request.getEmail());
            verificationRepo.deleteAll(userVerificationList);

            User newUser = new User(
                    request.getName(),
                    request.getEmail(),
                    passwordEncoder.encode(request.getPassword()),
                    Instant.now()
            );

            userRepo.save(newUser);

            // Authenticate email and password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserPrincipal userDetails = (UserPrincipal) userDetailsService.loadUserByUsername(request.getEmail());

            log.info("User registered successfully: userId={}", userDetails.getUserId());
            // Issue token and return response
            return issueTokens(userDetails, "User registered successfully");
        }
        throw new InvalidVerificationCode("Invalid verification code! Try again");
    }

    @Transactional
    public void logout(String refreshToken){
        String tokenHash = jwtUtil.hashToken(refreshToken);
        RefreshToken storedRefreshToken = refreshTokenRepo.findByTokenHash(tokenHash)
                        .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        log.info("User logged out successfully: userId={}", storedRefreshToken.getUserId());
        refreshTokenRepo.delete(storedRefreshToken);
    }

    private TokenResponse issueTokens(UserPrincipal userDetails, String message) {
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = UUID.randomUUID().toString();

        RefreshToken token = RefreshToken.builder()
                .tokenHash(jwtUtil.hashToken(refreshToken))
                .userId(userDetails.getUserId())
                .familyId(UUID.randomUUID())
                .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        refreshTokenRepo.save(token);

        return new TokenResponse(message, accessToken, refreshToken);
    }
}
