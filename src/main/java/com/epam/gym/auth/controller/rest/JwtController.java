package com.epam.gym.auth.controller.rest;

import com.epam.gym.auth.controller.rest.dto.request.GenerateTokenRequest;
import com.epam.gym.auth.controller.rest.dto.response.LoginResponse;
import com.epam.gym.auth.controller.rest.dto.response.ValidateResponse;
import com.epam.gym.auth.facade.jwt.IJwtFacade;
import com.epam.gym.auth.metrics.annotation.Measured;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/token")
@RequiredArgsConstructor
public class JwtController {

    private final IJwtFacade jwtFacade;

    @PostMapping
    @Measured("POST_internal_v1_jwt_generate")
    public LoginResponse generateToken(@RequestBody GenerateTokenRequest request) {
        return jwtFacade.generateToken(request);
    }

    @GetMapping
    @Measured("GET_internal_v1_jwt_validate")
    public ValidateResponse validateToken(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader
    ) {
        return jwtFacade.validate(authHeader);
    }
}
