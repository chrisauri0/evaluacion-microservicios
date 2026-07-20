package com.example.auth_server.service;

import com.example.auth_server.client.MicroservicioRestriccionesClient;
import com.example.auth_server.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final MicroservicioRestriccionesClient restriccionesClient;

    public CustomUserDetailsService(
            UsuarioRepository usuarioRepository,
            MicroservicioRestriccionesClient restriccionesClient
    ) {
        this.usuarioRepository = usuarioRepository;
        this.restriccionesClient = restriccionesClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (usuario.getId() != null && isUsuarioBaneado(usuario.getId())) {
            throw new DisabledException("Usuario bloqueado por administracion");
        }

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol())
                .build();
    }

    private boolean isUsuarioBaneado(Long usuarioId) {
        try {
            Map<String, Boolean> estado = restriccionesClient.verificarEstadoUsuario(usuarioId);
            return Boolean.TRUE.equals(estado.get("baneado"));
        } catch (Exception ex) {
            throw new AuthenticationServiceException("No se pudo validar el estado de baneo del usuario", ex);
        }
    }
}
