package com.akshansh.timecapsulebackend.model.dto;

import com.akshansh.timecapsulebackend.model.entity.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CapsuleContentDto {
    private UUID id;
    private ContentType type;
    private String body;        // null if type is IMAGE or FILE
    private String fileUrl;     // null if type is TEXT
    private String addedByName;
    private LocalDateTime addedAt;
}
