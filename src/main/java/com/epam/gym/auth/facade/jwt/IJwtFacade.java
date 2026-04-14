package com.epam.gym.auth.facade.jwt;

import com.epam.gym.auth.controller.rest.dto.request.GenerateTokenRequest;
import com.epam.gym.auth.controller.rest.dto.response.LoginResponse;
import com.epam.gym.auth.controller.rest.dto.response.ValidateResponse;
import lombok.NonNull;

public interface IJwtFacade {

    LoginResponse generateToken(@NonNull GenerateTokenRequest request);

    ValidateResponse validate(@NonNull String authHeader);
}
