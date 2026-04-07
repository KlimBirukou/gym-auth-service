package com.epam.gym.gymauthserver.service.protection;

import com.epam.gym.gymauthserver.configuration.properties.AuthProperties;
import com.epam.gym.gymauthserver.controller.rest.dto.response.BruteForceStatusResponse;
import com.epam.gym.gymauthserver.domain.LoginAttempt;
import com.epam.gym.gymauthserver.repository.ILoginAttemptRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BruteForceProtectionService implements IProtectionService {

    private final ILoginAttemptRepository loginAttemptRepository;
    private final AuthProperties authProperties;

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
        attempt.recordFailure();
        loginAttemptRepository.save(attempt);
    }

    private BruteForceStatusResponse resolveStatus(LoginAttempt attempt) {
        var maxAttempts = authProperties.maxLoginAttempts();
        var blockDuration = authProperties.blockDuration();
        if (attempt.isExpired(maxAttempts, blockDuration)) {
            attempt.reset();
            loginAttemptRepository.save(attempt);
            return BruteForceStatusResponse.notBlocked();
        }
        return attempt.isBlocked(maxAttempts, blockDuration)
            ? BruteForceStatusResponse.blocked(attempt.minutesUntilUnblock(blockDuration))
            : BruteForceStatusResponse.notBlocked();
    }
}
