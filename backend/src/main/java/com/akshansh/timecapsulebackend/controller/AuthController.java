package com.akshansh.timecapsulebackend.controller;

import com.akshansh.timecapsulebackend.exception.ResourceNotFoundException;
import com.akshansh.timecapsulebackend.model.dto.LoginRequest;
import com.akshansh.timecapsulebackend.model.dto.LoginResponse;
import com.akshansh.timecapsulebackend.model.dto.RegisterUserRequest;
import com.akshansh.timecapsulebackend.model.dto.UserDto;
import com.akshansh.timecapsulebackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "APIs for authentication")
public class AuthController {

    private final AuthService authService;


    @Operation(summary = "Register the user", description = "Register the user and add details in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = UserDto.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(
            @Valid @RequestBody RegisterUserRequest request
    ){
        UserDto newUser = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }


    @Operation(summary = "Login the user", description = "Sign in the user and generate JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successfully",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ){
        LoginResponse loginResp = authService.loginUser(request);

        Cookie cookie = new Cookie("refreshToken", loginResp.getRefreshToken());
        cookie.setHttpOnly(true);       // http-only cookie
        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.OK).body(loginResp);
    }

    @Operation(summary = "Generate new access token", description = "Generate new access token with the help of refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successfully",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(HttpServletRequest request){
        if(request.getCookies() == null){
            throw new ResourceNotFoundException("Refresh token not found in cookies");
        }

        String refreshToken = Arrays.stream(request.getCookies()) //getCookies() method returns a array of cookie
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(()-> new AuthenticationServiceException("RefreshToken not found"));
        LoginResponse loginResponseDto = authService.refreshToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }
}
