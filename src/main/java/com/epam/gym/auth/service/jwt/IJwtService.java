package com.epam.gym.auth.service.jwt;

import com.epam.gym.auth.controller.rest.dto.request.GenerateTokenRequest;
import com.epam.gym.auth.controller.rest.dto.response.LoginResponse;
import com.epam.gym.auth.controller.rest.dto.response.ValidateResponse;
import lombok.NonNull;

public interface IJwtService {

    LoginResponse generateToken(@NonNull GenerateTokenRequest request);

    ValidateResponse validate(String authHeader);
}
