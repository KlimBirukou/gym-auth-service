package com.epam.gym.gymauthserver.facade;

import com.epam.gym.gymauthserver.configuration.properties.AuthProperties;
import com.epam.gym.gymauthserver.controller.rest.dto.response.BruteForceStatusResponse;
import com.epam.gym.gymauthserver.controller.rest.dto.response.LoginResponse;
import com.epam.gym.gymauthserver.controller.rest.dto.response.ValidateResponse;
import com.epam.gym.gymauthserver.domain.LoginAttempt;
import com.epam.gym.gymauthserver.repository.ILoginAttemptRepository;
import com.epam.gym.gymauthserver.service.jwt.IJwtService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFacade implements IAuthFacade {

    private final IJwtService jwtService;
    private final ILoginAttemptRepository loginAttemptRepository;
    private final AuthProperties authProperties;

    @Override
    public BruteForceStatusResponse getBruteForceStatus(@NonNull UUID userUid) {
        log.debug("Get brute-force status. UserUid={}", userUid);
        var attempt = loginAttemptRepository.findByUserUid(userUid)
            .orElseGet(() -> LoginAttempt.builder().userUid(userUid).build());
        if (attempt.isExpired(authProperties.maxLoginAttempts(), authProperties.blockDuration())) {
            log.debug("Block expired, resetting. UserUid={}", userUid);
            attempt.reset();
            loginAttemptRepository.save(attempt);
            return BruteForceStatusResponse.notBlocked();
        }
        if (attempt.isBlocked(authProperties.maxLoginAttempts(), authProperties.blockDuration())) {
            var minutes = attempt.minutesUntilUnblock(authProperties.blockDuration());
            log.debug("User is blocked. UserUid={}, minutesLeft={}", userUid, minutes);
            return BruteForceStatusResponse.blocked(minutes);
        }

        return BruteForceStatusResponse.notBlocked();
    }

    @Override
    public void recordFailedAttempt(@NonNull UUID userUid) {
        log.debug("Record failed attempt. UserUid={}", userUid);
        var attempt = loginAttemptRepository.findByUserUid(userUid)
            .orElseGet(() -> LoginAttempt.builder().userUid(userUid).build());
        attempt.recordFailure();
        loginAttemptRepository.save(attempt);
        log.debug("Failed attempts recorded. UserUid={}, count={}", userUid, attempt.getFailedAttempts());
    }

    @Override
    public LoginResponse generateToken(@NonNull String username, @NonNull UUID userUid) {
        log.debug("Generate token. Username={}", username);
        loginAttemptRepository.deleteByUserUid(userUid);
        var token = jwtService.generateToken(username);
        log.debug("Token generated. Username={}", username);
        return LoginResponse.builder()
            .tokenType("Bearer")
            .accessToken(token)
            .expiresIn(authProperties.jwtExpiration())
            .build();
    }

    @Override
    public ValidateResponse validate(@NonNull String token) {
        log.debug("Validate token.");
        if (!jwtService.isTokenValid(token)) {
            log.debug("Token is invalid.");
            return ValidateResponse.invalid();
        }
        var username = jwtService.extractUsername(token);
        log.debug("Token valid. Username={}", username);
        return ValidateResponse.valid(username);
    }
}
