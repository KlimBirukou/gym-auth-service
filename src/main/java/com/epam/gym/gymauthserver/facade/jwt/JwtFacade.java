package com.epam.gym.gymauthserver.facade.jwt;

import com.epam.gym.gymauthserver.controller.rest.dto.request.GenerateTokenRequest;
import com.epam.gym.gymauthserver.controller.rest.dto.response.LoginResponse;
import com.epam.gym.gymauthserver.controller.rest.dto.response.ValidateResponse;
import com.epam.gym.gymauthserver.service.jwt.IJwtService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtFacade implements IJwtFacade{

    private final IJwtService jwtService;

    @Override
    public LoginResponse generateToken(@NonNull GenerateTokenRequest request) {
        log.info("Generate token. Started. Username={}", request.username());
        var response = jwtService.generateToken(request);
        log.info("Generate token. Finished. Username={}", request.username());
        return response;
    }

    @Override
    public ValidateResponse validate(String authHeader) {
        log.info("Validate token. Started.");
        var response = jwtService.validate(authHeader);
        log.info("Validate token. Finished. Username={}, valid={}", response.username(), response.valid());
        return response;
    }
}
