package com.epam.gym.gymauthserver.service.jwt;

import lombok.NonNull;

public interface IJwtService {

    String generateToken(@NonNull String username);

    String extractUsername(@NonNull String token);

    boolean isTokenValid(@NonNull String token);
}
