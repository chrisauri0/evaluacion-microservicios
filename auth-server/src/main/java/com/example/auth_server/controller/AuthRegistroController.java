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

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(request.getUsername());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setRol(request.getRol() != null ? request.getRol() : "ROLE_USER");
	nuevoUsuario.setEmail(request.getEmail());
	nuevoUsuario.setNombre(request.getNombre());
        nuevoUsuario.setFechaRegistro(LocalDate.now());

        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        
        return ResponseEntity.ok(guardado);
    }
}
