package com.epam.gym.gymauthserver.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.internal")
public record InternalProperties(
    String secret
) {

}
