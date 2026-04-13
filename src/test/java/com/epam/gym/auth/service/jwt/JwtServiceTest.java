package com.epam.gym.auth.service.jwt;

import com.epam.gym.auth.configuration.properties.AuthProperties;
import com.epam.gym.auth.controller.rest.dto.request.GenerateTokenRequest;
import com.epam.gym.auth.repository.domain.ILoginAttemptRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String USERNAME = "username";
    private static final String PREFIX = "Bearer";
    private static final long JWT_EXPIRATION = 3600L;
    private static final UUID USER_UID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final String INVALID_TOKEN = "invalid token";

    @Mock
    private ILoginAttemptRepository loginAttemptRepository;
    @Mock
    private AuthProperties authProperties;

    @InjectMocks
    private JwtService testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(loginAttemptRepository, authProperties);
    }

    @Test
    void generateToken_shouldReturnLoginResponse_whenRequestIsValid() {
        var secret = generateBase64Secret();
        var request = buildRequest();

        doNothing().when(loginAttemptRepository).deleteByUserUid(USER_UID);
        doReturn(PREFIX).when(authProperties).prefix();
        doReturn(secret).when(authProperties).secret();
        doReturn(JWT_EXPIRATION).when(authProperties).jwtExpiration();

        var result = testObject.generateToken(request);

        assertNotNull(result);
        assertEquals(PREFIX, result.tokenType());
        assertEquals(JWT_EXPIRATION, result.expiresIn());
        assertNotNull(result.accessToken());
        verify(loginAttemptRepository).deleteByUserUid(USER_UID);
        verify(authProperties).prefix();
        verify(authProperties).secret();
        verify(authProperties, times(2)).jwtExpiration();
    }

    @ParameterizedTest
    @NullSource
    void generateToken_shouldThrowNullPointerException_whenRequestIsNull(GenerateTokenRequest request) {
        assertThrows(NullPointerException.class, () -> testObject.generateToken(request));
    }


    @Test
    void validate_shouldReturnValidResponse_whenAuthHeaderIsValid() {
        var secret = generateBase64Secret();
        var request = buildRequest();

        doNothing().when(loginAttemptRepository).deleteByUserUid(USER_UID);
        doReturn(PREFIX).when(authProperties).prefix();
        doReturn(secret).when(authProperties).secret();
        doReturn(JWT_EXPIRATION).when(authProperties).jwtExpiration();
        var token = testObject.generateToken(request).accessToken();

        var result = testObject.validate(PREFIX + token);

        assertTrue(result.valid());
        assertEquals(USERNAME, result.username());
        verify(loginAttemptRepository).deleteByUserUid(USER_UID);
        verify(authProperties, times(3)).prefix();
        verify(authProperties, times(3)).secret();
        verify(authProperties, times(2)).jwtExpiration();
    }

    @Test
    void validate_shouldReturnInvalid_whenAuthHeaderIsNull() {
        var result = testObject.validate(null);

        assertFalse(result.valid());
    }

    @Test
    void validate_shouldReturnInvalid_whenAuthHeaderIsBlank() {
        var result = testObject.validate("   ");

        assertFalse(result.valid());
    }

    @Test
    void validate_shouldReturnInvalid_whenAuthHeaderHasNoPrefix() {
        doReturn(PREFIX).when(authProperties).prefix();

        var result = testObject.validate(INVALID_TOKEN);

        assertFalse(result.valid());
        verify(authProperties).prefix();
    }

    @Test
    void validate_shouldReturnInvalid_whenTokenIsMalformed() {
        var secret = generateBase64Secret();
        doReturn(PREFIX).when(authProperties).prefix();
        doReturn(secret).when(authProperties).secret();

        var result = testObject.validate(PREFIX + INVALID_TOKEN);

        assertFalse(result.valid());
        verify(authProperties, times(2)).prefix();
        verify(authProperties).secret();
    }

    private static GenerateTokenRequest buildRequest() {
        return GenerateTokenRequest.builder()
            .userUid(USER_UID)
            .username(USERNAME)
            .build();
    }

    private static String generateBase64Secret() {
        var key = new byte[64];
        new SecureRandom().nextBytes(key);
        return Base64.getUrlEncoder().encodeToString(key);
    }
}
