package com.akshansh.timecapsulebackend.controller;

import com.akshansh.timecapsulebackend.model.dto.*;
import com.akshansh.timecapsulebackend.service.CapsuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/capsules")
@RequiredArgsConstructor
@Tag(name = "Capsules Controller", description = "APIs for capsules management")
public class CapsuleController {

    private final CapsuleService capsuleService;

    @Operation(summary = "Create a new capsule", description = "Add a new capsule to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Capsule created successfully",
                    content = @Content(schema = @Schema(implementation = CapsuleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping
    public ResponseEntity<CapsuleDto> createCapsule(
            @RequestBody @Valid CreateCapsuleRequest request
    ){
        CapsuleDto createdCapsule = capsuleService.createCapsule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCapsule);
    }

    @Operation(summary = "Get all capsules for the current user", description = "Fetch and return all capsules for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Capsules retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<CapsuleDto>> getAllCapsulesForUser(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(required = false) String search
    ){
        Page<CapsuleDto> capsules = capsuleService.getAllCapsulesForUser(pageNo, pageSize, search);
        return ResponseEntity.status(HttpStatus.OK).body(capsules);
    }

    @Operation(summary = "Get capsule by ID", description = "Retrieve a capsule's details by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Capsule found",
                    content = @Content(schema = @Schema(implementation = CapsuleDto.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Capsule not found",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/{slug}")
    public ResponseEntity<CapsuleDto> getCapsuleDetails(@PathVariable String slug){
        CapsuleDto capsuleResponse = capsuleService.getCapsuleDetails(slug);
        return ResponseEntity.status(HttpStatus.OK).body(capsuleResponse);
    }

    @Operation(summary = "Update a capsule", description = "Update an existing capsule's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Capsule updated successfully",
                    content = @Content(schema = @Schema(implementation = CapsuleDto.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Capsule not found",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Capsule already unlocked",
                    content = @Content(schema = @Schema()))
    })
    @PutMapping("/{slug}")
    public ResponseEntity<CapsuleDto> updateCapsule(
            @RequestBody @Valid UpdateCapsuleRequest request,
            @PathVariable String slug
    ){
        CapsuleDto updatedCapsule = capsuleService.updateCapsule(request, slug);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCapsule);
    }

    @Operation(summary = "Delete a capsule", description = "Delete a capsule from the system using its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Capsule deleted successfully")
    })
    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deleteCapsule(@PathVariable String slug){
        capsuleService.deleteCapsule(slug);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add contents to a capsule", description = "Add a list of content to the capsule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contents added successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User/Capsule not found",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Capsule already unlocked",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/{slug}/contents")
    public ResponseEntity<Void> addContentToCapsules(
            @PathVariable String slug,
            @RequestBody @Valid @Size(min = 1, max = 10) List<AddContentRequestDto> requestDtoList
            ){
        capsuleService.addContentsToCapsule(requestDtoList, slug);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Get all contents of a capsule", description = "Fetch and return all the contents of a capsule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Capsules retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "409", description = "Capsule already unlocked",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/{slug}/contents")
    public ResponseEntity<Page<CapsuleContentDto>> getAllContentsForCapsule(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @PathVariable String slug
    ){
        Page<CapsuleContentDto> contentResponse = capsuleService.getAllContentsForCapsule(slug, pageNo, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(contentResponse);
    }
}
