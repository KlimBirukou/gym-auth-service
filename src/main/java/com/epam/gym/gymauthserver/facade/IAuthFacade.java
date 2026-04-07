package com.epam.gym.gymauthserver.facade;

import com.epam.gym.gymauthserver.controller.rest.dto.response.BruteForceStatusResponse;
import com.epam.gym.gymauthserver.controller.rest.dto.response.LoginResponse;
import com.epam.gym.gymauthserver.controller.rest.dto.response.ValidateResponse;
import lombok.NonNull;

import java.util.UUID;

public interface IAuthFacade {

    BruteForceStatusResponse getBruteForceStatus(@NonNull UUID userUid);

    void recordFailedAttempt(@NonNull UUID userUid);

    LoginResponse generateToken(@NonNull String username, @NonNull UUID userUid);

    ValidateResponse validate(@NonNull String token);
}
