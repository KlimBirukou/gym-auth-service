package com.epam.gym.auth.service.jwt;

import com.epam.gym.auth.configuration.properties.AuthProperties;
import com.epam.gym.auth.controller.rest.dto.request.GenerateTokenRequest;
import com.epam.gym.auth.controller.rest.dto.response.LoginResponse;
import com.epam.gym.auth.controller.rest.dto.response.ValidateResponse;
import com.epam.gym.auth.repository.domain.ILoginAttemptRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtService implements IJwtService {

    private final ILoginAttemptRepository loginAttemptRepository;
    private final AuthProperties authProperties;

    @Override
    public LoginResponse generateToken(@NonNull GenerateTokenRequest request) {
        loginAttemptRepository.deleteByUserUid(request.userUid());
        return LoginResponse.builder()
            .tokenType(authProperties.prefix())
            .accessToken(buildToken(request.username()))
            .expiresIn(authProperties.jwtExpiration())
            .build();
    }

    @Override
    public ValidateResponse validate(String authHeader) {
        return extractToken(authHeader)
            .filter(this::isTokenValid)
            .map(this::extractUsername)
            .map(ValidateResponse::valid)
            .orElse(ValidateResponse.invalid());
    }

    private String buildToken(String username) {
        var now = new Date();
        var expiry = new Date(now.getTime() + authProperties.jwtExpiration() * 1000L);
        return Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(signingKey())
            .compact();
    }

    private Optional<String> extractToken(String authHeader) {
        return Optional.ofNullable(authHeader)
            .filter(StringUtils::hasText)
            .filter(h -> h.startsWith(authProperties.prefix()))
            .map(h -> h.substring(authProperties.prefix().length()).trim());
    }

    private boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private String extractUsername(String token) {
        return Jwts.parser()
            .verifyWith(signingKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(authProperties.secret()));
    }
}
