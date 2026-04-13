package com.epam.gym.auth.service.protection;

import com.epam.gym.auth.controller.rest.dto.response.BruteForceStatusResponse;
import lombok.NonNull;

import java.util.UUID;

public interface IProtectionService {

    BruteForceStatusResponse getStatus(@NonNull UUID userUid);

    void recordFailedAttempt(@NonNull UUID userUid);
}
