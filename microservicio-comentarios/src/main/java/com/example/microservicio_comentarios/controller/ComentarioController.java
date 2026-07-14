package com.example.microservicio_comentarios.controller;

import com.example.microservicio_comentarios.dto.ComentarioRequest;
import com.example.microservicio_comentarios.dto.ComentarioResponse;
import com.example.microservicio_comentarios.service.ComentarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @PostMapping
    public ResponseEntity<ComentarioResponse> crear(@Valid @RequestBody ComentarioRequest request) {
        ComentarioResponse response = comentarioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/mis-comentarios")
    public ResponseEntity<List<ComentarioResponse>> misComentarios(@AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = jwt.getClaim("userId");
        return ResponseEntity.ok(comentarioService.listarPorUsuario(usuarioId));
    }

    @GetMapping
    public ResponseEntity<List<ComentarioResponse>> listarTodos() {
        return ResponseEntity.ok(comentarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComentarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(comentarioService.obtenerPorId(id));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ComentarioResponse>> listarPorPost(@PathVariable Long postId) {
        return ResponseEntity.ok(comentarioService.listarPorPost(postId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComentarioResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ComentarioRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = jwt.getClaim("userId");
        return ResponseEntity.ok(comentarioService.actualizar(id, request, usuarioId));
    }

   @DeleteMapping("/{id}")
        public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
            Long usuarioId = jwt.getClaim("userId");
            comentarioService.eliminar(id, usuarioId);
            return ResponseEntity.noContent().build();
        }
}