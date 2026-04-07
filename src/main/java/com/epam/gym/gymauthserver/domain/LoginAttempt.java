package com.epam.gym.gymauthserver.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public boolean isBlocked(int maxAttempts, @NonNull Duration blockDuration) {
        return checkStatus(maxAttempts, blockDuration, time -> time.isAfter(LocalDateTime.now()));
    }

    public boolean isExpired(int maxAttempts, @NonNull Duration blockDuration) {
        return checkStatus(maxAttempts, blockDuration, time -> !time.isAfter(LocalDateTime.now()));
    }

    private boolean checkStatus(int maxAttempts, Duration blockDuration, Predicate<LocalDateTime> timeCondition) {
        return Optional.ofNullable(lastFailedAt)
            .filter(last -> failedAttempts >= maxAttempts)
            .map(last -> last.plus(blockDuration))
            .map(timeCondition::test)
            .orElse(false);
    }

    public long minutesUntilUnblock(@NonNull Duration blockDuration) {
        return Optional.ofNullable(lastFailedAt)
            .map(last -> last.plus(blockDuration))
            .map(unblockTime -> ChronoUnit.MINUTES.between(LocalDateTime.now(), unblockTime) + 1)
            .filter(minutes -> minutes > 0)
            .orElse(0L);
    }

    public void recordFailure() {
        this.failedAttempts++;
        this.lastFailedAt = LocalDateTime.now();
    }

    public void reset() {
        this.failedAttempts = 0;
        this.lastFailedAt = null;
    }
}
