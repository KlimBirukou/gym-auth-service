package com.epam.gym.auth.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.internal")
public record InternalProperties(
    String secret,
    String headerName,
    String principal
) {

}
