package com.example.microservicio_usuarios.service;

import com.example.microservicio_usuarios.dto.UsuarioRequest; // Debes usar este
import com.example.microservicio_usuarios.entity.Usuario;    // Tu entidad ahora está aquí
import com.example.microservicio_usuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario crear(UsuarioRequest request) { // Corregido el parámetro
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("El email ya está registrado");
        }
        
        Usuario usuario = new Usuario();
        usuario.setId(request.getId());
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

    public Usuario actualizar(Long id, UsuarioRequest request) { // Corregido el parámetro
        Usuario usuario = obtenerPorId(id);
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}