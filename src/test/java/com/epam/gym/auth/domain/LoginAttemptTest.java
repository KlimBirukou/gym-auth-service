package com.epam.gym.auth.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginAttemptTest {

    private static final UUID USER_UID = UUID.randomUUID();
    private static final int MAX_ATTEMPTS = 3;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(10);

    private final Instant fixedInstant = Instant.parse("2026-04-04T10:00:00Z");
    private final Clock clock = Clock.fixed(fixedInstant, java.time.ZoneId.systemDefault());

    private LoginAttempt testObject;

    @BeforeEach
    void setUp() {
        testObject = LoginAttempt.builder()
            .userUid(USER_UID)
            .failedAttempts(0)
            .lastFailedAt(null)
            .build();
    }

    @Test
    void isBlocked_shouldReturnFalse_whenAttemptsBelowMax() {
        testObject.setFailedAttempts(MAX_ATTEMPTS - 1);
        testObject.setLastFailedAt(LocalDateTime.now(clock));

        assertFalse(testObject.isBlocked(MAX_ATTEMPTS, BLOCK_DURATION, clock));
    }

    @Test
    void isBlocked_shouldReturnTrue_whenAttemptsAtMaxAndWithinDuration() {
        testObject.setFailedAttempts(MAX_ATTEMPTS);
        testObject.setLastFailedAt(LocalDateTime.now(clock).minusMinutes(5));

        assertTrue(testObject.isBlocked(MAX_ATTEMPTS, BLOCK_DURATION, clock));
    }

    @Test
    void isBlocked_shouldReturnFalse_whenDurationPassed() {
        testObject.setFailedAttempts(MAX_ATTEMPTS);
        testObject.setLastFailedAt(LocalDateTime.now(clock).minusMinutes(11));

        assertFalse(testObject.isBlocked(MAX_ATTEMPTS, BLOCK_DURATION, clock));
    }

    @Test
    void isExpired_shouldReturnTrue_whenAttemptsReachedAndDurationPassed() {
        testObject.setFailedAttempts(MAX_ATTEMPTS);
        testObject.setLastFailedAt(LocalDateTime.now(clock).minusMinutes(11));

        assertTrue(testObject.isExpired(MAX_ATTEMPTS, BLOCK_DURATION, clock));
    }

    @Test
    void isExpired_shouldReturnFalse_whenStillInBlockPeriod() {
        testObject.setFailedAttempts(MAX_ATTEMPTS);
        testObject.setLastFailedAt(LocalDateTime.now(clock).minusMinutes(5));

        assertFalse(testObject.isExpired(MAX_ATTEMPTS, BLOCK_DURATION, clock));
    }

    @ParameterizedTest
    @CsvSource({
        "5, 5",
        "9, 1",
        "10, 0",
        "15, 0"
    })
    void minutesUntilUnblock_shouldReturnCorrectDuration(int minutesPassed, int expectedMinutesLeft) {
        testObject.setLastFailedAt(LocalDateTime.now(clock).minusMinutes(minutesPassed));

        Duration remaining = testObject.minutesUntilUnblock(BLOCK_DURATION, clock);

        assertEquals(expectedMinutesLeft, remaining.toMinutes());
    }

    @Test
    void recordFailure_shouldIncrementAttemptsAndSetCurrentTime() {
        LocalDateTime beforeCall = LocalDateTime.now(clock);

        testObject.recordFailure(clock);

        assertEquals(1, testObject.getFailedAttempts());
        assertEquals(beforeCall, testObject.getLastFailedAt());
    }

    @Test
    void reset_shouldClearAttemptsAndTime() {
        testObject.setFailedAttempts(5);
        testObject.setLastFailedAt(LocalDateTime.now(clock));

        testObject.reset();

        assertEquals(0, testObject.getFailedAttempts());
        assertNull(testObject.getLastFailedAt());
    }
}
