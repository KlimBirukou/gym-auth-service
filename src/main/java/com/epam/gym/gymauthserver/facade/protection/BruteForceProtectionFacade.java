package com.epam.gym.gymauthserver.facade.protection;

import com.epam.gym.gymauthserver.controller.rest.dto.response.BruteForceStatusResponse;
import com.epam.gym.gymauthserver.service.protection.IProtectionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BruteForceProtectionFacade implements IProtectionFacade {

    private final IProtectionService protectionService;

    @Override
    public BruteForceStatusResponse getStatus(@NonNull UUID userUid) {
        log.info("Get brute-force protection status. Started. UserUid={}", userUid);
        var response = protectionService.getStatus(userUid);
        log.info("Get brute-force protection status. Finished. UserUid={}, blocked={}", userUid, response.blocked());
        return response;
    }

    @Override
    public void recordFailedAttempt(@NonNull UUID userUid) {
        log.info("Record failed attempt. Started. UserUid={}", userUid);
        protectionService.recordFailedAttempt(userUid);
        log.info("Record failed attempt. Finished. UserUid={}", userUid);
    }
}
