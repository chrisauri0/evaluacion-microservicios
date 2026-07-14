package com.example.microservicio_posts.service;

import com.example.microservicio_posts.dto.PostRequest;
import com.example.microservicio_posts.dto.PostResponse;
import com.example.microservicio_posts.model.Post;
import com.example.microservicio_posts.repository.PostRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

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

    public PostResponse actualizar(Long id, PostRequest request, Long usuarioIdSolicitante) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post no encontrado con id: " + id));

        if (!post.getAutorId().equals(usuarioIdSolicitante)) {
            throw new AccessDeniedException("No puedes editar publicaciones de otro usuario");
        }

        post.setTitulo(request.getTitulo());
        post.setContenido(request.getContenido());
        post.setCategoria(request.getCategoria());
        return toResponse(postRepository.save(post));
    }

    public void eliminar(Long id, Long usuarioIdSolicitante) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post no encontrado con id: " + id));

        if (!post.getAutorId().equals(usuarioIdSolicitante)) {
            throw new AccessDeniedException("No puedes eliminar publicaciones de otro usuario");
        }

        postRepository.deleteById(id);
    }

    private PostResponse toResponse(Post p) {
        return new PostResponse(p.getId(), p.getTitulo(), p.getContenido(), p.getAutorId(), p.getCategoria(), p.getFechaCreacion());
    }
}