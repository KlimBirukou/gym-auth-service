package com.epam.gym.auth.controller.rest;

import com.epam.gym.auth.controller.rest.dto.response.BruteForceStatusResponse;
import com.epam.gym.auth.facade.protection.IProtectionFacade;
import com.epam.gym.auth.metrics.annotation.Measured;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/protection")
@RequiredArgsConstructor
public class ProtectionController {

    private final IProtectionFacade protectionFacade;

    @GetMapping("/{userUid}")
    @Measured("GET_internal_v1_protection_status")
    public BruteForceStatusResponse getStatus(@PathVariable UUID userUid) {
        return protectionFacade.getStatus(userUid);
    }

    @PostMapping("/{userUid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Measured("POST_internal_v1_protection_record_failure_attempt")
    public void recordFailedAttempt(@PathVariable UUID userUid) {
        protectionFacade.recordFailedAttempt(userUid);
    }
}
