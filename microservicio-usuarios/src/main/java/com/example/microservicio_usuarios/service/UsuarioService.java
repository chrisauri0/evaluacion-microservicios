package com.example.microservicio_usuarios.service;

import com.example.microservicio_usuarios.client.AuthClient;
import com.example.microservicio_usuarios.dto.AuthRegistroRequest;
import com.example.microservicio_usuarios.dto.UsuarioRequest;
import com.example.microservicio_usuarios.entity.Usuario;
import com.example.microservicio_usuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilUsuarioService perfilUsuarioService;
    private final AuthClient authClient;

    public UsuarioService(UsuarioRepository usuarioRepository, PerfilUsuarioService perfilUsuarioService, AuthClient authClient) {
        this.usuarioRepository = usuarioRepository;
        this.perfilUsuarioService = perfilUsuarioService;
        this.authClient = authClient;
    }

    public Usuario crear(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("El email ya está registrado");
        }
        
        // 1. Preparamos los datos para el auth-server
        AuthRegistroRequest authReq = new AuthRegistroRequest();
        authReq.setUsername(request.getUsername());
        authReq.setPassword(request.getPassword());
        authReq.setRol(request.getRol());
        authReq.setEmail(request.getEmail());
        authReq.setNombre(request.getNombre());

        Map<String, Object> authResponse = authClient.registrarCredenciales(authReq);
        
        Long authId = ((Number) authResponse.get("id")).longValue();

        Usuario usuario = new Usuario();
        usuario.setId(authId);
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setNombre(request.getNombre());
        usuario.setRol(request.getRol() != null ? request.getRol().replace("ROLE_", "") : "USER");

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = obtenerPorId(id);

        AuthRegistroRequest authReq = new AuthRegistroRequest();
        authReq.setUsername(request.getUsername());
        authReq.setRol(request.getRol());
        authReq.setEmail(request.getEmail());
        authReq.setNombre(request.getNombre());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            authReq.setPassword(request.getPassword());
        }

        authClient.actualizarCredenciales(id, authReq);
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            usuario.setUsername(request.getUsername());
        }
        if (request.getRol() != null && !request.getRol().isEmpty()) {
            usuario.setRol(request.getRol().replace("ROLE_", ""));
        }

        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        authClient.eliminarCredenciales(id);

        perfilUsuarioService.eliminarPerfilPorUsuarioId(id);
        usuarioRepository.deleteById(id);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    public Usuario obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

}
