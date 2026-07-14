package com.example.microservicio_posts.dto;


import java.time.LocalDateTime;

public class PostResponse {
    private Long id;
    private String titulo;
    private String contenido;
    private Long autorId;
    private String categoria;
    private LocalDateTime fechaCreacion;

    public PostResponse(Long id, String titulo, String contenido, Long autorId, String categoria, LocalDateTime fechaCreacion) {
        this.id = id;
        this.titulo = titulo;
        this.contenido = contenido;
        this.autorId = autorId;
        this.categoria = categoria;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getContenido() { return contenido; }
    public Long getAutorId() { return autorId; }
    public String getCategoria() { return categoria; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}