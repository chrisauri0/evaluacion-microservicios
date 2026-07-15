package com.example.microservicio_posts.service;

import com.example.microservicio_posts.dto.PublicacionRequest;
import com.example.microservicio_posts.dto.PublicacionResponse;
import com.example.microservicio_posts.model.Publicacion;
import com.example.microservicio_posts.repository.PublicacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;

    public PublicacionService(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    public PublicacionResponse crear(PublicacionRequest request) {
        Publicacion publicacion = new Publicacion(
                request.getTitulo(),
                request.getContenido(),
                request.getCategoria(),
                request.getAutorId()
        );
        Publicacion guardada = publicacionRepository.save(publicacion);
        return toResponse(guardada);
    }

    public List<PublicacionResponse> listarTodas() {
        return publicacionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PublicacionResponse obtenerPorId(Long id) {
        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicacion no encontrada con id: " + id));
        return toResponse(publicacion);
    }

    public List<PublicacionResponse> listarPorAutor(Long autorId) {
        return publicacionRepository.findByAutorId(autorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PublicacionResponse actualizar(Long id, PublicacionRequest request) {
        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicacion no encontrada con id: " + id));
        publicacion.setTitulo(request.getTitulo());
        publicacion.setContenido(request.getContenido());
        publicacion.setCategoria(request.getCategoria());
        Publicacion actualizada = publicacionRepository.save(publicacion);
        return toResponse(actualizada);
    }

    public void eliminar(Long id) {
        if (!publicacionRepository.existsById(id)) {
            throw new RuntimeException("Publicacion no encontrada con id: " + id);
        }
        publicacionRepository.deleteById(id);
    }

    private PublicacionResponse toResponse(Publicacion p) {
        return new PublicacionResponse(
                p.getId(), p.getTitulo(), p.getContenido(), p.getCategoria(), p.getAutorId(), p.getFechaCreacion()
        );
    }
}
