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

        // 2. Disparamos la petición Feign
        Map<String, Object> authResponse = authClient.registrarCredenciales(authReq);
        
        // 3. Obtenemos el ID que nos generó el auth-server (viene en el JSON de respuesta)
        Long authId = ((Number) authResponse.get("id")).longValue();

        // 4. Guardamos el perfil en nuestra base de datos con ese mismo ID
        Usuario usuario = new Usuario();
        usuario.setId(authId);
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setNombre(request.getNombre());
        
        return usuarioRepository.save(usuario);
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

    public Usuario actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = obtenerPorId(id);
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        perfilUsuarioService.eliminarPerfilPorUsuarioId(id);
        usuarioRepository.deleteById(id);
    }
}
