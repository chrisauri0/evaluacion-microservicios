package com.example.auth_server.config;

import com.example.auth_server.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class JwtCustomizerConfig {

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer(UsuarioRepository usuarioRepository) {
        return context -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                var authentication = context.getPrincipal();

                List<String> roles = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(auth -> auth.replace("ROLE_", ""))
                        .collect(Collectors.toList());

                context.getClaims().claim("roles", roles);

                // Agregamos el userId real buscando por username
                String username = authentication.getName();
                usuarioRepository.findByUsername(username).ifPresent(usuario -> {
                    context.getClaims().claim("userId", usuario.getId());
                });
            }
        };
    }
}