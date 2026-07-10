package com.example.auth_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class JwtCustomizerConfig {

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return context -> {
            // Solo customizamos el access token, no el ID token ni refresh token
            if ("access_token".equals(context.getTokenType().getValue())) {
                var authentication = context.getPrincipal();

                List<String> roles = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(auth -> auth.replace("ROLE_", "")) // quitamos el prefijo ROLE_ para el claim
                        .collect(Collectors.toList());

                context.getClaims().claim("roles", roles);
            }
        };
    }
}