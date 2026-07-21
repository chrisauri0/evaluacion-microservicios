package com.example.microservicio_usuarios.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;

@Entity
@Table(name = "perfiles_usuario")
@Data
@NoArgsConstructor
public class PerfilUsuario {

    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "foto_perfil", nullable = false)
    private String fotoPerfil;

    @Column(name = "nombre_perfil", nullable = false)
    private String nombrePerfil;

    @Column(name = "descripcion_personal", length = 280)
    private String descripcionPersonal;

    @Column(name = "email_privado", nullable = false)
    private boolean emailPrivado;

    @Column(name = "nombre_privado", nullable = false)
    private boolean nombrePrivado;

    @Column(name = "foto_perfil_solicitada")
    private String fotoPerfilSolicitada;

    @Column(name = "nombre_perfil_solicitado")
    private String nombrePerfilSolicitado;

    @Column(name = "descripcion_personal_solicitada", length = 280)
    private String descripcionPersonalSolicitada;

    @Column(name = "email_privado_solicitado")
    private Boolean emailPrivadoSolicitado;

    @Column(name = "nombre_privado_solicitado")
    private Boolean nombrePrivadoSolicitado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_moderacion", nullable = false)
    private EstadoModeracionPerfil estadoModeracion;

    @Column(name = "observacion_moderacion", length = 300)
    private String observacionModeracion;

    @Column(name = "ultima_actualizacion", nullable = false)
    private LocalDateTime ultimaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.ultimaActualizacion = LocalDateTime.now(Clock.systemUTC());
    }

    @PreUpdate
    protected void onUpdate() {
        this.ultimaActualizacion = LocalDateTime.now(Clock.systemUTC());
    }
}
