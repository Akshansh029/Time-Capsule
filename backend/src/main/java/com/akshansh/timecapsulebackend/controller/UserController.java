package com.akshansh.timecapsulebackend.controller;

import com.akshansh.timecapsulebackend.model.dto.ActiveUserResponse;
import com.akshansh.timecapsulebackend.model.dto.UserDto;
import com.akshansh.timecapsulebackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(
            @RequestParam String q
    ){
        List<UserDto> searchedUsers = userService.searchUsers(q);
        return ResponseEntity.status(HttpStatus.OK).body(searchedUsers);
    }
}
