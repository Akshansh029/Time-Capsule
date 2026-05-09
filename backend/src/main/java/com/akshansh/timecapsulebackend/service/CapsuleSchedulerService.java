package com.akshansh.timecapsulebackend.service;

import com.akshansh.timecapsulebackend.model.entity.Capsule;
import com.akshansh.timecapsulebackend.model.entity.CapsuleStatus;
import com.akshansh.timecapsulebackend.model.entity.UserVerification;
import com.akshansh.timecapsulebackend.repository.CapsuleRepository;
import com.akshansh.timecapsulebackend.repository.UserVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CapsuleSchedulerService {

    private final CapsuleRepository capsuleRepository;
    private final EmailService emailService;
    private final UserVerificationRepository userVerificationRepository;

    @Transactional
    @Scheduled(fixedDelay = 60000)      // runs every 60 secs
    public void unlockDueCapsules(){
        List<Capsule> dueCapsules = capsuleRepository
                .findAllDueCapsulesWithDetails(LocalDateTime.now());


        for(Capsule capsule : dueCapsules){
            capsule.setStatus(CapsuleStatus.UNLOCKED);
            capsuleRepository.save(capsule);

            log.info("Capsule: {}, unlocked from scheduler at: {}", capsule.getSlug(), LocalDateTime.now());

            // Send emails to capsule members
            emailService.sendUnlockNotification(capsule);
        }
    }

    @Transactional
    @Scheduled(fixedDelay = (1000L * 60 * 60 * 6))        // runs every 6 hours
    public void deleteExpiredVerificationCodes(){
        List<UserVerification> expiredCodes = userVerificationRepository.findAllByExpiresAtBefore(LocalDateTime.now());

        userVerificationRepository.deleteAll(expiredCodes);
        log.info("SCHEDULER: Deleted all expired codes in UserVerification table");
    }
}
