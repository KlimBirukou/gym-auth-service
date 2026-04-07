package com.epam.gym.gymauthserver.controller.rest;

import com.epam.gym.gymauthserver.controller.rest.dto.request.GenerateTokenRequest;
import com.epam.gym.gymauthserver.controller.rest.dto.response.BruteForceStatusResponse;
import com.epam.gym.gymauthserver.controller.rest.dto.response.LoginResponse;
import com.epam.gym.gymauthserver.controller.rest.dto.response.ValidateResponse;
import com.epam.gym.gymauthserver.facade.IAuthFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/auth")
@RequiredArgsConstructor
public class InternalAuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final IAuthFacade authFacade;

    @GetMapping("/brute-force/{userUid}")
    public BruteForceStatusResponse getBruteForceStatus(@PathVariable UUID userUid) {
        return authFacade.getBruteForceStatus(userUid);
    }

    @PostMapping("/attempt/{userUid}")
    public void recordFailedAttempt(@PathVariable UUID userUid) {
        authFacade.recordFailedAttempt(userUid);
    }

    @PostMapping("/token")
    public LoginResponse generateToken(@RequestBody GenerateTokenRequest request) {
        return authFacade.generateToken(request.username(), request.userUid());
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(
        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            return ResponseEntity.ok(ValidateResponse.invalid());
        }
        var token = authHeader.substring(BEARER_PREFIX.length());
        return ResponseEntity.ok(authFacade.validate(token));
    }
}


