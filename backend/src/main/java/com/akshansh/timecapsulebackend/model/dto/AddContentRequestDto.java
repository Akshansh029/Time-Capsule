package com.akshansh.timecapsulebackend.model.dto;

import com.akshansh.timecapsulebackend.model.entity.Capsule;
import com.akshansh.timecapsulebackend.model.entity.ContentType;
import com.akshansh.timecapsulebackend.model.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddContentRequestDto {
    @NotNull(message = "Content type is required")
    private ContentType type;

    private String body;

    private String fileUrl;
}
