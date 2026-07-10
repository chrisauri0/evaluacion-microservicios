package com.example.microservicio_comentarios.dto;

import java.time.LocalDateTime;

public class ComentarioResponse {
    private Long id;
    private String contenido;
    private Long postId;
    private Long usuarioId;
    private LocalDateTime fechaCreacion;

    public ComentarioResponse(Long id, String contenido, Long postId, Long usuarioId, LocalDateTime fechaCreacion) {
        this.id = id;
        this.contenido = contenido;
        this.postId = postId;
        this.usuarioId = usuarioId;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public String getContenido() { return contenido; }
    public Long getPostId() { return postId; }
    public Long getUsuarioId() { return usuarioId; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}