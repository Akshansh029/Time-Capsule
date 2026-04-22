package com.akshansh.timecapsulebackend.mapper;

import com.akshansh.timecapsulebackend.model.dto.CapsuleDto;
import com.akshansh.timecapsulebackend.model.entity.Capsule;
import org.springframework.stereotype.Component;

@Component
public class CapsuleMapper {

    public static CapsuleDto toDto(Capsule capsule){
        return new CapsuleDto(
                capsule.getId(),
                capsule.getTitle(),
                capsule.getStatus(),
                capsule.getUnlockDate(),
                capsule.isPrivate()
        );
    }
}
