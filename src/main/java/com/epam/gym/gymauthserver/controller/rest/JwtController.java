package com.epam.gym.gymauthserver.controller.rest;

import com.epam.gym.gymauthserver.controller.rest.dto.request.GenerateTokenRequest;
import com.epam.gym.gymauthserver.controller.rest.dto.response.LoginResponse;
import com.epam.gym.gymauthserver.controller.rest.dto.response.ValidateResponse;
import com.epam.gym.gymauthserver.facade.jwt.IJwtFacade;
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
    public LoginResponse generateToken(@RequestBody GenerateTokenRequest request) {
        return jwtFacade.generateToken(request);
    }

    @GetMapping
    public ValidateResponse validateToken(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader
    ) {
        return jwtFacade.validate(authHeader);
    }
}
