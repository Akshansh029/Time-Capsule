package com.akshansh.timecapsulebackend.model.dto;

import com.akshansh.timecapsulebackend.model.entity.ContentType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddContentRequestDto {
    @NotNull(message = "Content type is required")
    private ContentType type;

    private String body;

    private String fileUrl;
}
