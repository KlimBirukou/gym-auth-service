package com.epam.gym.gymauthserver.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "application.security.endpoints")
public record SecurityProperties(
    List<String> publicEndpoints,
    String internalPattern,
    String internalPrefix
) {

}
