package com.example.microservicio_usuarios.service;

import com.example.microservicio_usuarios.dto.UsuarioRequest; // Debes usar este
import com.example.microservicio_usuarios.dto.UsuarioDisponibleResponse;
import com.example.microservicio_usuarios.entity.Usuario;    // Tu entidad ahora está aquí
import com.example.microservicio_usuarios.entity.PerfilUsuario;
import com.example.microservicio_usuarios.repository.PerfilUsuarioRepository;
import com.example.microservicio_usuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilUsuarioService perfilUsuarioService;
    private final PerfilUsuarioRepository perfilUsuarioRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PerfilUsuarioService perfilUsuarioService,
            PerfilUsuarioRepository perfilUsuarioRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.perfilUsuarioService = perfilUsuarioService;
        this.perfilUsuarioRepository = perfilUsuarioRepository;
    }

    public Usuario crear(UsuarioRequest request) {
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

    public List<UsuarioDisponibleResponse> listarUsuariosDisponibles() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        Map<Long, PerfilUsuario> perfilesPorUsuarioId = perfilUsuarioRepository.findAll().stream()
                .collect(Collectors.toMap(PerfilUsuario::getUsuarioId, Function.identity()));

        return usuarios.stream()
                .map(usuario -> {
                    PerfilUsuario perfil = perfilesPorUsuarioId.get(usuario.getId());
                    String fotoPerfil = perfil != null ? perfil.getFotoPerfil() : "AVATAR_1";

                    return UsuarioDisponibleResponse.builder()
                            .id(usuario.getId())
                            .username(usuario.getUsername())
                            .email(usuario.getEmail())
                            .nombre(usuario.getNombre())
                            .fotoPerfil(fotoPerfil)
                            .build();
                })
                .toList();
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
        Usuario actualizado = usuarioRepository.save(usuario);

        perfilUsuarioRepository.findById(id).ifPresent(perfil -> {
            perfil.setNombrePerfil(actualizado.getNombre());
            perfilUsuarioRepository.save(perfil);
        });

        return actualizado;
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        perfilUsuarioService.eliminarPerfilPorUsuarioId(id);
        usuarioRepository.deleteById(id);
    }
}