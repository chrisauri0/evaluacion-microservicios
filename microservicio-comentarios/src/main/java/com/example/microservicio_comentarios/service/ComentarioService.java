package com.example.microservicio_comentarios.service;

import com.example.microservicio_comentarios.dto.ComentarioRequest;
import com.example.microservicio_comentarios.dto.ComentarioResponse;
import com.example.microservicio_comentarios.model.Comentario;
import com.example.microservicio_comentarios.repository.ComentarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    public ComentarioResponse crear(ComentarioRequest request) {
        Comentario comentario = new Comentario(
                request.getContenido(),
                request.getPostId(),
                request.getUsuarioId()
        );
        Comentario guardado = comentarioRepository.save(comentario);
        return toResponse(guardado);
    }

    public List<ComentarioResponse> listarTodos() {
        return comentarioRepository.findAll().stream()
                .map(arg0 -> toResponse(arg0))
                .collect(Collectors.toList());
    }

    public ComentarioResponse obtenerPorId(Long id) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con id: " + id));
        return toResponse(comentario);
    }

    public List<ComentarioResponse> listarPorPost(Long postId) {
        return comentarioRepository.findByPostId(postId).stream()
                .map(arg0 -> toResponse(arg0))
                .collect(Collectors.toList());
    }

    public ComentarioResponse actualizar(Long id, ComentarioRequest request) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con id: " + id));
        comentario.setContenido(request.getContenido());
        Comentario actualizado = comentarioRepository.save(comentario);
        return toResponse(actualizado);
    }

    public void eliminar(Long id) {
        if (!comentarioRepository.existsById(id)) {
            throw new RuntimeException("Comentario no encontrado con id: " + id);
        }
        comentarioRepository.deleteById(id);
    }

    private ComentarioResponse toResponse(Comentario c) {
        return new ComentarioResponse(
                c.getId(), c.getContenido(), c.getPostId(), c.getUsuarioId(), c.getFechaCreacion()
        );
    }
}