package com.epam.gym.gymauthserver.facade.protection;

import com.epam.gym.gymauthserver.controller.rest.dto.response.BruteForceStatusResponse;
import lombok.NonNull;

import java.util.UUID;

public interface IProtectionFacade {

    BruteForceStatusResponse getStatus(@NonNull UUID userUid);

    void recordFailedAttempt(@NonNull UUID userUid);
}
