package com.akshansh.timecapsulebackend.service;

import com.akshansh.timecapsulebackend.model.entity.Capsule;
import com.akshansh.timecapsulebackend.model.entity.CapsuleStatus;
import com.akshansh.timecapsulebackend.model.entity.UserVerification;
import com.akshansh.timecapsulebackend.repository.CapsuleRepository;
import com.akshansh.timecapsulebackend.repository.RefreshTokenRepository;
import com.akshansh.timecapsulebackend.repository.UserVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final CapsuleRepository capsuleRepository;
    private final UserVerificationRepository userVerificationRepository;
    private final RefreshTokenRepository refreshTokenRepo;
    private final ResendEmailService resendEmailService;

    @Transactional
    @Scheduled(fixedDelay = 60000)      // runs every 60 secs
    public void unlockDueCapsules(){
        List<Capsule> dueCapsules = capsuleRepository
                .findAllDueCapsulesWithDetails(Instant.now());


        for(Capsule capsule : dueCapsules){
            capsule.setStatus(CapsuleStatus.UNLOCKED);
            capsuleRepository.save(capsule);

            log.info("event=capsuleUnlockedFromScheduler capsuleId={} unlockDate={}",
                    capsule.getId(), capsule.getUnlockDate());

            // Send emails to capsule members
            resendEmailService.sendUnlockNotification(capsule);
        }
    }

    @Transactional
    @Scheduled(fixedDelay = (1000L * 60 * 60 * 6))        // runs every 6 hours
    public void deleteExpiredVerificationCodes(){
        List<UserVerification> expiredCodes = userVerificationRepository.findAllByExpiresAtBefore(Instant.now());

        userVerificationRepository.deleteAll(expiredCodes);
        log.info("event=expiredVerificationCodesDeleted expiredCodesCount={}", expiredCodes.size());
    }

    @Transactional
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteExpiredRefreshTokens(){
        refreshTokenRepo.deleteByExpiresAtBefore(Instant.now());
        log.info("event=expiredRefreshTokensDeleted");
    }
}
