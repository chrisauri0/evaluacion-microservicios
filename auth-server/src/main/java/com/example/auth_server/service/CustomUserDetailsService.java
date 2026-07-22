package com.example.auth_server.service;

import com.example.auth_server.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        String rol = usuario.getRol() != null ? usuario.getRol().replace("ROLE_", "") : "USER";

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(rol)
                .build();
    }
}
