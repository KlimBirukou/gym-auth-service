package com.epam.gym.gymauthserver.controller.rest.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record BruteForceStatusResponse(
    boolean blocked,
    long minutesLeft
) {

    public static BruteForceStatusResponse notBlocked() {
        return new BruteForceStatusResponse(false, 0);
    }

    public static BruteForceStatusResponse blocked(long minutesLeft) {
        return new BruteForceStatusResponse(true, minutesLeft);
    }
}
