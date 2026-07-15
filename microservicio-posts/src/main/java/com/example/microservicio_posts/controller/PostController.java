package com.example.microservicio_posts.controller;

import com.example.microservicio_posts.dto.PostRequest;
import com.example.microservicio_posts.dto.PostResponse;
import com.example.microservicio_posts.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> crear(@Valid @RequestBody PostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> listarTodos() {
        return ResponseEntity.ok(postService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(postService.obtenerPorId(id));
    }

    @GetMapping("/autor/{autorId}")
    public ResponseEntity<List<PostResponse>> listarPorAutor(@PathVariable Long autorId) {
        return ResponseEntity.ok(postService.listarPorAutor(autorId));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<PostResponse>> listarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(postService.listarPorCategoria(categoria));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponse>> obtenerFeedAmigos(@AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = jwt.getClaim("userId");
        List<PostResponse> feed = postService.listarFeedAmigos(usuarioId);
        return ResponseEntity.ok(feed);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> actualizar(@PathVariable Long id, @Valid @RequestBody PostRequest request,
        @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = jwt.getClaim("userId");
        return ResponseEntity.ok(postService.actualizar(id, request, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = jwt.getClaim("userId");
        postService.eliminar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}