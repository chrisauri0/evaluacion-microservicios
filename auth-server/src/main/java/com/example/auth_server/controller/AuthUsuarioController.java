package com.example.auth_server.controller;

import com.example.auth_server.dto.RegistroRequest;
import com.example.auth_server.entity.Usuario;
import com.example.auth_server.service.AuthUsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auth/usuarios")
public class AuthUsuarioController {

    private final AuthUsuarioService authUsuarioService;

    public AuthUsuarioController(AuthUsuarioService authUsuarioService) {
        this.authUsuarioService = authUsuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(authUsuarioService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authUsuarioService.crear(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        authUsuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}