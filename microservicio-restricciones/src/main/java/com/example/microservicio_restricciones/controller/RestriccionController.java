package com.example.microservicio_restricciones.controller;


import com.example.microservicio_restricciones.dto.BanearUsuarioRequest;
import com.example.microservicio_restricciones.dto.RestringirPostRequest;
import com.example.microservicio_restricciones.model.PublicacionRestringida;
import com.example.microservicio_restricciones.model.UsuarioBaneado;
import com.example.microservicio_restricciones.service.RestriccionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restricciones")
public class RestriccionController {

    private final RestriccionService restriccionService;

    public RestriccionController(RestriccionService restriccionService) {
        this.restriccionService = restriccionService;
    }

    // ---------- Posts ----------

    @PostMapping("/posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublicacionRestringida> restringirPost(@RequestBody RestringirPostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restriccionService.restringirPost(request));
    }

    @GetMapping("/posts/{postId}/estado")
    public ResponseEntity<Map<String, Boolean>> verificarPost(@PathVariable Long postId) {
        return ResponseEntity.ok(Map.of("restringido", restriccionService.estaRestringido(postId)));
    }

    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> quitarRestriccionPost(@PathVariable Long postId) {
        restriccionService.quitarRestriccion(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PublicacionRestringida>> listarPostsRestringidos() {
        return ResponseEntity.ok(restriccionService.listarPostsRestringidos());
    }

    // ---------- Usuarios ----------

    @PostMapping("/usuarios/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioBaneado> banearUsuario(@RequestBody BanearUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restriccionService.banearUsuario(request));
    }

    @GetMapping("/usuarios/{usuarioId}/estado")
    public ResponseEntity<Map<String, Boolean>> verificarUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(Map.of("baneado", restriccionService.estaBaneado(usuarioId)));
    }

    @DeleteMapping("/usuarios/{usuarioId}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> quitarBaneo(@PathVariable Long usuarioId) {
        restriccionService.quitarBaneo(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuarios/baneados")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioBaneado>> listarUsuariosBaneados() {
        return ResponseEntity.ok(restriccionService.listarUsuariosBaneados());
    }
}