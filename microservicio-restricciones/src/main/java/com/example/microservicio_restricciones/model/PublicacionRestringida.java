package com.example.microservicio_restricciones.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "publicaciones_restringidas")
public class PublicacionRestringida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false, unique = true)
    private Long postId;

    @Column(name = "autor_id", nullable = false)
    private Long autorId;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Column(name = "fecha_restriccion", nullable = false, updatable = false)
    private LocalDateTime fechaRestriccion;

    @PrePersist
    protected void onCreate() {
        this.fechaRestriccion = LocalDateTime.now();
    }

    public PublicacionRestringida() {}

    public PublicacionRestringida(Long postId, Long autorId, Long adminId, String motivo) {
        this.postId = postId;
        this.autorId = autorId;
        this.adminId = adminId;
        this.motivo = motivo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getAutorId() { return autorId; }
    public void setAutorId(Long autorId) { this.autorId = autorId; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDateTime getFechaRestriccion() { return fechaRestriccion; }
    public void setFechaRestriccion(LocalDateTime fechaRestriccion) { this.fechaRestriccion = fechaRestriccion; }
}