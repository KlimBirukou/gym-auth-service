package com.epam.gym.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class LoginAttempt {

    private UUID userUid;
    private int failedAttempts;
    private LocalDateTime lastFailedAt;

    public boolean isBlocked(int maxAttempts, @NonNull Duration blockDuration, @NonNull Clock clock) {
        return checkStatus(
            maxAttempts,
            blockDuration,
            clock,
            unblockTime -> unblockTime.isAfter(LocalDateTime.now(clock)));
    }

    public boolean isExpired(int maxAttempts, @NonNull Duration blockDuration, @NonNull Clock clock) {
        return checkStatus(maxAttempts,
            blockDuration,
            clock,
            unblockTime -> !unblockTime.isAfter(LocalDateTime.now(clock)));
    }

    private boolean checkStatus(int maxAttempts,
                                Duration blockDuration,
                                Clock clock,
                                Predicate<LocalDateTime> timeCondition) {
        return Optional.ofNullable(lastFailedAt)
            .filter(last -> failedAttempts >= maxAttempts)
            .map(last -> last.plus(blockDuration))
            .map(timeCondition::test)
            .orElse(false);
    }

    public Duration minutesUntilUnblock(@NonNull Duration blockDuration, @NonNull Clock clock) {
        return Optional.ofNullable(lastFailedAt)
            .map(last -> last.plus(blockDuration))
            .map(unblockTime -> Duration.between(LocalDateTime.now(clock), unblockTime))
            .filter(remaining -> !remaining.isNegative())
            .orElse(Duration.ZERO);
    }

    public void recordFailure(@NonNull Clock clock) {
        this.failedAttempts++;
        this.lastFailedAt = LocalDateTime.now(clock);
    }

    public void reset() {
        this.failedAttempts = 0;
        this.lastFailedAt = null;
    }
}
