package com.example.microservicio_posts.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.microservicio_posts.client.ClientFeignUsuarios;
import com.example.microservicio_posts.dto.PostRequest;
import com.example.microservicio_posts.dto.PostResponse;
import com.example.microservicio_posts.model.Post;
import com.example.microservicio_posts.repository.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ClientFeignUsuarios clientFeignUsuarios;

    public PostResponse crear(PostRequest request) {
        Post post = new Post(request.getTitulo(), request.getContenido(), request.getAutorId(), request.getCategoria());
        return toResponse(postRepository.save(post));
    }

    public List<PostResponse> listarTodos() {
        return postRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PostResponse obtenerPorId(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post no encontrado con id: " + id));
        return toResponse(post);
    }

    public List<PostResponse> listarPorAutor(Long autorId) {
        return postRepository.findByAutorId(autorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<PostResponse> listarPorCategoria(String categoria) {
        return postRepository.findByCategoria(categoria).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<PostResponse> listarFeedAmigos(Long usuarioId) {
        List<Long> amigosIds = clientFeignUsuarios.obtenerAmigosIds(usuarioId);
        
        if (amigosIds == null || amigosIds.isEmpty()) {
            return List.of();
        }

        return postRepository.findAll().stream()
                .filter(post -> amigosIds.contains(post.getAutorId()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PostResponse actualizar(Long id, PostRequest request, Long usuarioIdSolicitante, boolean esAdmin) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post no encontrado con id: " + id));

        if (!esAdmin && !post.getAutorId().equals(usuarioIdSolicitante)) {
            throw new AccessDeniedException("No puedes editar publicaciones de otro usuario");
        }

        post.setTitulo(request.getTitulo());
        post.setContenido(request.getContenido());
        post.setCategoria(request.getCategoria());
        return toResponse(postRepository.save(post));
    }

    public void eliminar(Long id, Long usuarioIdSolicitante, boolean esAdmin) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post no encontrado con id: " + id));

        if (!esAdmin && !post.getAutorId().equals(usuarioIdSolicitante)) {
            throw new AccessDeniedException("No puedes eliminar publicaciones de otro usuario");
        }

        postRepository.deleteById(id);
    }

    private PostResponse toResponse(Post p) {
        return new PostResponse(p.getId(), p.getTitulo(), p.getContenido(), p.getAutorId(), p.getCategoria(), p.getFechaCreacion());
    }

    public List<PostResponse> listarMisPublicaciones(Long usuarioId, String categoria, LocalDate desde, LocalDate hasta) {
    return postRepository.findByAutorId(usuarioId).stream()
            .filter(post -> categoria == null || post.getCategoria().equalsIgnoreCase(categoria))
            .filter(post -> desde == null || !post.getFechaCreacion().toLocalDate().isBefore(desde))
            .filter(post -> hasta == null || !post.getFechaCreacion().toLocalDate().isAfter(hasta))
            .map(this::toResponse)
            .collect(Collectors.toList());
}
}

//perras
