package com.epam.gym.auth.controller.rest;

import com.epam.gym.auth.controller.rest.dto.request.GenerateTokenRequest;
import com.epam.gym.auth.controller.rest.dto.response.LoginResponse;
import com.epam.gym.auth.controller.rest.dto.response.ValidateResponse;
import com.epam.gym.auth.facade.jwt.IJwtFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class JwtControllerTest {

    private static final String AUTH_HEADER = "Bearer some-token";
    private static final String USERNAME = "tester";
    private static final UUID USER_UID = UUID.randomUUID();

    private static final GenerateTokenRequest REQUEST = new GenerateTokenRequest(USERNAME, USER_UID);
    private static final LoginResponse LOGIN_RESPONSE = new LoginResponse("at", "Bearer", 3600);
    private static final ValidateResponse VALIDATE_RESPONSE = ValidateResponse.valid(USERNAME);

    @Mock
    private IJwtFacade jwtFacade;

    @InjectMocks
    private JwtController testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(jwtFacade);
    }

    @Test
    void generateToken_shouldReturnLoginResponse() {
        doReturn(LOGIN_RESPONSE).when(jwtFacade).generateToken(REQUEST);

        var actual = testObject.generateToken(REQUEST);

        assertNotNull(actual);
        assertEquals(LOGIN_RESPONSE, actual);
        verify(jwtFacade).generateToken(REQUEST);
    }

    @Test
    void validateToken_shouldReturnValidateResponse() {
        doReturn(VALIDATE_RESPONSE).when(jwtFacade).validate(AUTH_HEADER);

        var actual = testObject.validateToken(AUTH_HEADER);

        assertNotNull(actual);
        assertEquals(VALIDATE_RESPONSE, actual);
        verify(jwtFacade).validate(AUTH_HEADER);
    }
}
