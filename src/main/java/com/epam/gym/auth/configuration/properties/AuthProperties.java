package com.epam.gym.auth.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "application.auth")
public record AuthProperties(
    int maxLoginAttempts,
    Duration blockDuration,
    long jwtExpiration,
    String secret,
    String prefix
) {

}
