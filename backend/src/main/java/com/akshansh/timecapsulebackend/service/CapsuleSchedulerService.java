package com.akshansh.timecapsulebackend.service;

import com.akshansh.timecapsulebackend.model.entity.Capsule;
import com.akshansh.timecapsulebackend.model.entity.CapsuleStatus;
import com.akshansh.timecapsulebackend.repository.CapsuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CapsuleSchedulerService {

    private final CapsuleRepository capsuleRepository;

    @Scheduled(fixedDelay = 60000)      // runs every 60 secs
    public void unlockDueCapsules(){
        List<Capsule> dueCapsules = capsuleRepository
                .findAllByStatusAndUnlockDateBefore(CapsuleStatus.LOCKED, LocalDateTime.now());

        for(Capsule capsule : dueCapsules){
            capsule.setStatus(CapsuleStatus.UNLOCKED);
            capsuleRepository.save(capsule);

            // Send emails to capsule members
        }
    }
}
