package com.epam.gym.gymauthserver.configuration;

import com.epam.gym.gymauthserver.configuration.properties.SecurityProperties;
import com.epam.gym.gymauthserver.controller.context.InternalSecretFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final InternalSecretFilter internalSecretFilter;
    private final SecurityProperties securityProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(securityProperties.publicEndpoints().toArray(String[]::new)).permitAll()
                .requestMatchers(securityProperties.internalPattern()).authenticated()
                .anyRequest().denyAll())
            .addFilterBefore(internalSecretFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}


