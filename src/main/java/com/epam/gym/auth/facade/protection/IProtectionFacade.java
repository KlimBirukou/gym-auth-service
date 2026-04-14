package com.epam.gym.auth.facade.protection;

import com.epam.gym.auth.controller.rest.dto.response.BruteForceStatusResponse;
import lombok.NonNull;

import java.util.UUID;

public interface IProtectionFacade {

    BruteForceStatusResponse getStatus(@NonNull UUID userUid);

    void recordFailedAttempt(@NonNull UUID userUid);
}
