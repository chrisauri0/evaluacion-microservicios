package com.example.auth_server.controller;

import com.example.auth_server.dto.RegistroAuthRequest;
import com.example.auth_server.entity.Usuario;
import com.example.auth_server.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/auth-internal")
public class AuthRegistroController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthRegistroController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> registrarCredenciales(@RequestBody RegistroAuthRequest request) {
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        // Limpiamos el rol para evitar conflictos con el prefijo automático de Spring Security
        String rolLimpio = "USER";
        if (request.getRol() != null && !request.getRol().isEmpty()) {
            rolLimpio = request.getRol().replace("ROLE_", "");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(request.getUsername());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setRol(rolLimpio);
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setNombre(request.getNombre());
        nuevoUsuario.setFechaRegistro(LocalDate.now());

        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        
        return ResponseEntity.ok(guardado);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> actualizarCredenciales(@PathVariable Long id, @RequestBody RegistroAuthRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en auth-server"));
        
        if (request.getUsername() != null) {
            usuario.setUsername(request.getUsername());
        }
        if (request.getRol() != null && !request.getRol().isEmpty()) {
            usuario.setRol(request.getRol().replace("ROLE_", ""));
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        usuarioRepository.save(usuario);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> eliminarCredenciales(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }


}
