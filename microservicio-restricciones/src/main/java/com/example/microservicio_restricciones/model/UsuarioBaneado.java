package com.example.microservicio_restricciones.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_baneados")
public class UsuarioBaneado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false, unique = true)
    private Long usuarioId;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Column(name = "fecha_baneo", nullable = false, updatable = false)
    private LocalDateTime fechaBaneo;

    @PrePersist
    protected void onCreate() {
        this.fechaBaneo = LocalDateTime.now();
    }

    public UsuarioBaneado() {}

    public UsuarioBaneado(Long usuarioId, Long adminId, String motivo) {
        this.usuarioId = usuarioId;
        this.adminId = adminId;
        this.motivo = motivo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDateTime getFechaBaneo() { return fechaBaneo; }
    public void setFechaBaneo(LocalDateTime fechaBaneo) { this.fechaBaneo = fechaBaneo; }
}