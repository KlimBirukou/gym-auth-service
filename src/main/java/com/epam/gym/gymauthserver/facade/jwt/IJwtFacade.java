package com.epam.gym.gymauthserver.facade.jwt;

import com.epam.gym.gymauthserver.controller.rest.dto.request.GenerateTokenRequest;
import com.epam.gym.gymauthserver.controller.rest.dto.response.LoginResponse;
import com.epam.gym.gymauthserver.controller.rest.dto.response.ValidateResponse;
import lombok.NonNull;

public interface IJwtFacade {

    LoginResponse generateToken(@NonNull GenerateTokenRequest request);

    ValidateResponse validate(String authHeader);
}
