package com.epam.gym.gymauthserver.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "application.cors")
public record CorsProperties(
    List<String> allowedOrigins
) {

}
