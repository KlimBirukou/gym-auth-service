package com.epam.gym.gymauthserver.controller.context;

import com.epam.gym.gymauthserver.configuration.properties.InternalProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternalSecretFilter extends OncePerRequestFilter {

    public static final String INTERNAL_SECRET_HEADER = "X-Internal-Secret";

    private final InternalProperties internalProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/internal/")) {
            String requestSecret = request.getHeader(INTERNAL_SECRET_HEADER);

            if (internalProperties.secret().equals(requestSecret)) {
                var auth = new UsernamePasswordAuthenticationToken(
                    "INTERNAL_SERVICE", null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.warn("Rejected internal request without valid secret. URI={}", request.getRequestURI());
            }
        }

        filterChain.doFilter(request, response);
    }
}
