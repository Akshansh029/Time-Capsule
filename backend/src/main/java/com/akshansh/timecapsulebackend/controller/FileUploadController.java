package com.akshansh.timecapsulebackend.controller;

import com.akshansh.timecapsulebackend.exception.FileDownloadException;
import com.akshansh.timecapsulebackend.exception.FileEmptyException;
import com.akshansh.timecapsulebackend.exception.FileUploadException;
import com.akshansh.timecapsulebackend.exception.ResourceNotFoundException;
import com.akshansh.timecapsulebackend.model.dto.CapsuleDto;
import com.akshansh.timecapsulebackend.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/files")
@Validated
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Upload Controller", description = "APIs for file upload and download")
public class FileUploadController {

    private final FileService fileService;

    @Operation(summary = "Upload a file", description = "Upload a new file in S3 bucket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "File uploaded successfully",
                    content = @Content(schema = @Schema(implementation = CapsuleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws FileEmptyException, FileUploadException, IOException {
        if (multipartFile.isEmpty()) {
            throw new FileEmptyException("File is empty. Cannot save an empty file");
        }

        boolean isValidFile = isValidFile(multipartFile);
        List<String> allowedFileExtensions = new ArrayList<>(Arrays.asList("pdf", "ppt", "doc", "docx", "txt", "epub", "csv", "png", "jpg", "jpeg", "srt"));

        if (isValidFile && allowedFileExtensions.contains(FilenameUtils.getExtension(multipartFile.getOriginalFilename()))){
            String fileName = fileService.uploadFile(multipartFile);

            return new ResponseEntity<>(fileName, HttpStatus.CREATED);
        } else {
            throw new FileUploadException("Invalid File. File extension or File name is not supported");
        }
    }

    @Operation(summary = "Download a file", description = "Download a file from S3 bucket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "File downloaded successfully",
                    content = @Content(schema = @Schema(implementation = CapsuleDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestParam @NotBlank @NotNull String key) {
        try {
            byte[] fileBytes = fileService.downloadFile(key);

            ByteArrayResource resource = new ByteArrayResource(fileBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileBytes.length)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
                    .body(resource);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (FileDownloadException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isValidFile(MultipartFile multipartFile){
        log.info("Empty Status ==> {}", multipartFile.isEmpty());
        if (Objects.isNull(multipartFile.getOriginalFilename())){
            return false;
        }
        return !multipartFile.getOriginalFilename().trim().equals("");
    }
}
