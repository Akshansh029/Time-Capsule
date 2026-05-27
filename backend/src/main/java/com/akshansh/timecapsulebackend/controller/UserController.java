package com.akshansh.timecapsulebackend.controller;

import com.akshansh.timecapsulebackend.model.dto.ActiveUserResponse;
import com.akshansh.timecapsulebackend.model.dto.UpdateUsernameRequestDto;
import com.akshansh.timecapsulebackend.model.dto.UserDto;
import com.akshansh.timecapsulebackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for user management")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get Current User Details", description = "Get current user's details from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details fetched successfully",
                    content = @Content(schema = @Schema(implementation = ActiveUserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/me")
    public ResponseEntity<ActiveUserResponse> getActiveUserDetails(){
        ActiveUserResponse userDetails = userService.getActiveUserDetails();
        return ResponseEntity.status(HttpStatus.OK).body(userDetails);
    }

    @Operation(summary = "Update user's username", description = "Update the username of the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username updated successfully",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "User not found",
                    content = @Content(schema = @Schema()))
    })
    @PatchMapping("/username")
    public ResponseEntity<UserDto> updateUsername(
            @Valid @RequestBody UpdateUsernameRequestDto request){
        UserDto updatedUser = userService.updateUser(request);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete user", description = "Delete full user account from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "User not found",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(){
        userService.deleteUser();
        return ResponseEntity.noContent().build();
    }
}
