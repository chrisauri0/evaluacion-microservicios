package com.example.microservicio_usuarios.controller;

import com.example.microservicio_usuarios.dto.ModeracionPerfilRequest;
import com.example.microservicio_usuarios.dto.PerfilUsuarioResponse;
import com.example.microservicio_usuarios.dto.PerfilUsuarioUpdateRequest;
import com.example.microservicio_usuarios.service.PerfilUsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class PerfilUsuarioController {

    private final PerfilUsuarioService perfilUsuarioService;

    public PerfilUsuarioController(PerfilUsuarioService perfilUsuarioService) {
        this.perfilUsuarioService = perfilUsuarioService;
    }

    @GetMapping("/{id}/perfil")
    public ResponseEntity<PerfilUsuarioResponse> obtenerPerfil(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(perfilUsuarioService.obtenerPerfil(id, jwt));
    }

    @PutMapping("/{id}/perfil")
    public ResponseEntity<PerfilUsuarioResponse> actualizarPerfil(
            @PathVariable Long id,
            @Valid @RequestBody PerfilUsuarioUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(perfilUsuarioService.actualizarPerfil(id, request, jwt));
    }

    @GetMapping("/perfiles/moderacion/pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PerfilUsuarioResponse>> listarPendientes(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(perfilUsuarioService.listarPendientes(jwt));
    }

    @PatchMapping("/perfiles/{id}/moderacion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PerfilUsuarioResponse> moderarPerfil(
            @PathVariable Long id,
            @Valid @RequestBody ModeracionPerfilRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(perfilUsuarioService.moderarPerfil(id, request, jwt));
    }
}
