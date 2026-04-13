package com.epam.gym.auth.facade.jwt;

import com.epam.gym.auth.controller.rest.dto.request.GenerateTokenRequest;
import com.epam.gym.auth.controller.rest.dto.response.LoginResponse;
import com.epam.gym.auth.controller.rest.dto.response.ValidateResponse;
import com.epam.gym.auth.service.jwt.IJwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class JwtFacadeTest {

    private static final String AUTH_HEADER = "Bearer token";
    private static final GenerateTokenRequest REQUEST = new GenerateTokenRequest("user", UUID.randomUUID());
    private static final LoginResponse LOGIN_RESPONSE = new LoginResponse("at", "Bearer", 3600);
    private static final ValidateResponse VALIDATE_RESPONSE = ValidateResponse.valid("user");

    @Mock
    private IJwtService jwtService;

    @InjectMocks
    private JwtFacade testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void generateToken_shouldDelegateToService() {
        doReturn(LOGIN_RESPONSE).when(jwtService).generateToken(REQUEST);

        var result = testObject.generateToken(REQUEST);

        assertEquals(LOGIN_RESPONSE, result);
        verify(jwtService).generateToken(REQUEST);
    }

    @ParameterizedTest
    @NullSource
    void generateToken_shouldThrowException(GenerateTokenRequest request) {
        assertThrows(NullPointerException.class, () -> testObject.generateToken(request));
    }

    @Test
    void validate_shouldDelegateToService() {
        doReturn(VALIDATE_RESPONSE).when(jwtService).validate(AUTH_HEADER);

        var result = testObject.validate(AUTH_HEADER);

        assertEquals(VALIDATE_RESPONSE, result);
        verify(jwtService).validate(AUTH_HEADER);
    }

    @ParameterizedTest
    @NullSource
    void validate_shouldThrowException(String authHeader) {
        assertThrows(NullPointerException.class, () -> testObject.validate(authHeader));
    }
}
