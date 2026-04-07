package com.epam.gym.gymauthserver.service.protection;

import com.epam.gym.gymauthserver.controller.rest.dto.response.BruteForceStatusResponse;
import lombok.NonNull;

import java.util.UUID;

public interface IProtectionService {

    BruteForceStatusResponse getStatus(@NonNull UUID userUid);

    void recordFailedAttempt(@NonNull UUID userUid);
}
