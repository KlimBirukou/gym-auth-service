package com.epam.gym.gymauthserver.service.protection;

import com.epam.gym.gymauthserver.configuration.properties.AuthProperties;
import com.epam.gym.gymauthserver.controller.rest.dto.response.BruteForceStatusResponse;
import com.epam.gym.gymauthserver.domain.LoginAttempt;
import com.epam.gym.gymauthserver.repository.domain.ILoginAttemptRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BruteForceProtectionService implements IProtectionService {

    private final ILoginAttemptRepository loginAttemptRepository;
    private final AuthProperties authProperties;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    public BruteForceStatusResponse getStatus(@NonNull UUID userUid) {
        return loginAttemptRepository.findByUserUid(userUid)
            .map(this::resolveStatus)
            .orElse(BruteForceStatusResponse.notBlocked());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailedAttempt(@NonNull UUID userUid) {
        var attempt = loginAttemptRepository.findByUserUid(userUid)
            .orElseGet(() -> LoginAttempt.builder().userUid(userUid).build());
        attempt.recordFailure(clock);
        loginAttemptRepository.save(attempt);
    }

    private BruteForceStatusResponse resolveStatus(LoginAttempt attempt) {
        var maxAttempts = authProperties.maxLoginAttempts();
        var blockDuration = authProperties.blockDuration();
        if (attempt.isExpired(maxAttempts, blockDuration, clock)) {
            attempt.reset();
            loginAttemptRepository.save(attempt);
            return BruteForceStatusResponse.notBlocked();
        }
        return attempt.isBlocked(maxAttempts, blockDuration, clock)
            ? BruteForceStatusResponse.blocked(attempt.minutesUntilUnblock(blockDuration, clock).toMinutes())
            : BruteForceStatusResponse.notBlocked();
    }
}
