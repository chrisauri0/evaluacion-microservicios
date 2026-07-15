package com.example.microservicio_posts.dto;

import java.time.LocalDateTime;

public class PublicacionResponse {
    private Long id;
    private String titulo;
    private String contenido;
    private String categoria;
    private Long autorId;
    private LocalDateTime fechaCreacion;

    public PublicacionResponse(Long id, String titulo, String contenido, String categoria,
                                Long autorId, LocalDateTime fechaCreacion) {
        this.id = id;
        this.titulo = titulo;
        this.contenido = contenido;
        this.categoria = categoria;
        this.autorId = autorId;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getContenido() { return contenido; }
    public String getCategoria() { return categoria; }
    public Long getAutorId() { return autorId; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
