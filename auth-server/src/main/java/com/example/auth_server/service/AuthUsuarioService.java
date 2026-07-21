package com.example.auth_server.service;

import com.example.auth_server.client.MicroservicioUsuariosClient;
import com.example.auth_server.dto.RegistroRequest;
import com.example.auth_server.dto.UsuarioProfileRequest;
import com.example.auth_server.entity.Usuario;
import com.example.auth_server.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class AuthUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MicroservicioUsuariosClient usuariosClient;

    public AuthUsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, MicroservicioUsuariosClient usuariosClient) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuariosClient = usuariosClient;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario crear(RegistroRequest request) {
        // 1. Guardar en Auth-Server para generar el ID y asegurar credenciales
        Usuario authUser = new Usuario();
        authUser.setUsername(request.getUsername());
        authUser.setEmail(request.getEmail());
        authUser.setNombre(request.getNombre());
        authUser.setPassword(passwordEncoder.encode(request.getPassword()));
        authUser.setRol(request.getRol());
        authUser.setFechaRegistro(LocalDate.now());
        
        authUser = usuarioRepository.save(authUser); // Se genera el ID

        // 2. Avisar a microservicio-usuarios que cree el perfil
        UsuarioProfileRequest profileRequest = new UsuarioProfileRequest();
        profileRequest.setId(authUser.getId());
        profileRequest.setUsername(authUser.getUsername());
        profileRequest.setEmail(authUser.getEmail());
        profileRequest.setNombre(authUser.getNombre());
        
        usuariosClient.crearUsuarioProfile(profileRequest);

        return authUser;
    }

    @Transactional
    public Usuario actualizar(Long id, RegistroRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));

        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setNombre(request.getNombre());
        usuario.setRol(request.getRol());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        usuario = usuarioRepository.save(usuario);

        UsuarioProfileRequest profileRequest = new UsuarioProfileRequest();
        profileRequest.setId(usuario.getId());
        profileRequest.setUsername(usuario.getUsername());
        profileRequest.setEmail(usuario.getEmail());
        profileRequest.setNombre(usuario.getNombre());

        usuariosClient.actualizarUsuarioProfile(id, profileRequest);

        return usuario;
    }

    @Transactional
    public void eliminar(Long id) {
        // 1. Eliminar perfil y cascada en el microservicio de usuarios
        usuariosClient.eliminarUsuarioProfile(id);
        // 2. Eliminar credenciales en auth-server
        usuarioRepository.deleteById(id);
    }
}