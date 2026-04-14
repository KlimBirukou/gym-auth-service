package com.epam.gym.auth.service.protection;

import com.epam.gym.auth.configuration.properties.AuthProperties;
import com.epam.gym.auth.domain.LoginAttempt;
import com.epam.gym.auth.repository.domain.ILoginAttemptRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class BruteForceProtectionServiceTest {

    private static final UUID USER_UID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(30);
    private static final Duration MINUTES_UNTIL_UNBLOCK = Duration.ofMinutes(15);
    private final Instant fixedInstant = Instant.parse("2026-04-04T10:00:00Z");

    @Spy
    private final Clock clock = Clock.fixed(fixedInstant, ZoneId.systemDefault());
    @Mock
    private ILoginAttemptRepository loginAttemptRepository;
    @Mock
    private AuthProperties authProperties;
    @Mock
    private LoginAttempt loginAttempt;

    @InjectMocks
    private BruteForceProtectionService testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(loginAttemptRepository, authProperties, loginAttempt);
    }

    @Test
    void getStatus_shouldReturnNotBlocked_whenNoLoginAttemptExists() {
        doReturn(Optional.empty()).when(loginAttemptRepository).findByUserUid(USER_UID);

        var result = testObject.getStatus(USER_UID);

        assertNotNull(result);
        assertFalse(result.blocked());
        verify(loginAttemptRepository).findByUserUid(USER_UID);
    }

    @Test
    void getStatus_shouldReturnNotBlockedAndResetAttempt_whenAttemptIsExpired() {
        doReturn(Optional.of(loginAttempt)).when(loginAttemptRepository).findByUserUid(USER_UID);
        doReturn(MAX_ATTEMPTS).when(authProperties).maxLoginAttempts();
        doReturn(BLOCK_DURATION).when(authProperties).blockDuration();
        doReturn(true).when(loginAttempt).isExpired(MAX_ATTEMPTS, BLOCK_DURATION, clock);
        doNothing().when(loginAttempt).reset();
        doNothing().when(loginAttemptRepository).save(loginAttempt);

        var result = testObject.getStatus(USER_UID);

        assertNotNull(result);
        assertFalse(result.blocked());
        verify(loginAttemptRepository).findByUserUid(USER_UID);
        verify(authProperties).maxLoginAttempts();
        verify(authProperties).blockDuration();
        verify(loginAttempt).isExpired(MAX_ATTEMPTS, BLOCK_DURATION, clock);
        verify(loginAttempt).reset();
        verify(loginAttemptRepository).save(loginAttempt);
    }

    @Test
    void getStatus_shouldReturnBlocked_whenAttemptIsNotExpiredAndBlocked() {
        doReturn(Optional.of(loginAttempt)).when(loginAttemptRepository).findByUserUid(USER_UID);
        doReturn(MAX_ATTEMPTS).when(authProperties).maxLoginAttempts();
        doReturn(BLOCK_DURATION).when(authProperties).blockDuration();
        doReturn(false).when(loginAttempt).isExpired(MAX_ATTEMPTS, BLOCK_DURATION, clock);
        doReturn(true).when(loginAttempt).isBlocked(MAX_ATTEMPTS, BLOCK_DURATION, clock);
        doReturn(MINUTES_UNTIL_UNBLOCK).when(loginAttempt).minutesUntilUnblock(BLOCK_DURATION, clock);

        var result = testObject.getStatus(USER_UID);

        assertNotNull(result);
        assertTrue(result.blocked());
        verify(loginAttemptRepository).findByUserUid(USER_UID);
        verify(authProperties).maxLoginAttempts();
        verify(authProperties).blockDuration();
        verify(loginAttempt).isExpired(MAX_ATTEMPTS, BLOCK_DURATION, clock);
        verify(loginAttempt).isBlocked(MAX_ATTEMPTS, BLOCK_DURATION, clock);
        verify(loginAttempt).minutesUntilUnblock(BLOCK_DURATION, clock);
    }

    @Test
    void getStatus_shouldReturnNotBlocked_whenAttemptIsNotExpiredAndNotBlocked() {
        doReturn(Optional.of(loginAttempt)).when(loginAttemptRepository).findByUserUid(USER_UID);
        doReturn(MAX_ATTEMPTS).when(authProperties).maxLoginAttempts();
        doReturn(BLOCK_DURATION).when(authProperties).blockDuration();
        doReturn(false).when(loginAttempt).isExpired(MAX_ATTEMPTS, BLOCK_DURATION, clock);
        doReturn(false).when(loginAttempt).isBlocked(MAX_ATTEMPTS, BLOCK_DURATION, clock);

        var result = testObject.getStatus(USER_UID);

        assertNotNull(result);
        assertFalse(result.blocked());
        verify(loginAttemptRepository).findByUserUid(USER_UID);
        verify(authProperties).maxLoginAttempts();
        verify(authProperties).blockDuration();
        verify(loginAttempt).isExpired(MAX_ATTEMPTS, BLOCK_DURATION, clock);
        verify(loginAttempt).isBlocked(MAX_ATTEMPTS, BLOCK_DURATION, clock);
    }

    @ParameterizedTest
    @NullSource
    void getStatus_shouldThrowNullPointerException_whenUserUidIsNull(UUID userUid) {
        assertThrows(NullPointerException.class, () -> testObject.getStatus(userUid));
    }

    @Test
    void recordFailedAttempt_shouldCreateNewAttemptAndSave_whenNoExistingAttemptFound() {
        doReturn(Optional.empty()).when(loginAttemptRepository).findByUserUid(USER_UID);
        doNothing().when(loginAttemptRepository).save(any(LoginAttempt.class));

        testObject.recordFailedAttempt(USER_UID);

        verify(loginAttemptRepository).findByUserUid(USER_UID);
        verify(loginAttemptRepository).save(any(LoginAttempt.class));
    }

    @Test
    void recordFailedAttempt_shouldUpdateExistingAttemptAndSave_whenAttemptAlreadyExists() {
        doReturn(Optional.of(loginAttempt)).when(loginAttemptRepository).findByUserUid(USER_UID);
        doNothing().when(loginAttempt).recordFailure(clock);
        doNothing().when(loginAttemptRepository).save(loginAttempt);

        testObject.recordFailedAttempt(USER_UID);

        verify(loginAttemptRepository).findByUserUid(USER_UID);
        verify(loginAttempt).recordFailure(clock);
        verify(loginAttemptRepository).save(loginAttempt);
    }

    @ParameterizedTest
    @NullSource
    void recordFailedAttempt_shouldThrowNullPointerException_whenUserUidIsNull(UUID userUid) {
        assertThrows(NullPointerException.class, () -> testObject.recordFailedAttempt(userUid));
    }
}
