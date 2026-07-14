package com.example.microservicio_usuarios.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;

@Entity
@Table(name = "revision_perfil")
@Data
@NoArgsConstructor
public class RevisionPerfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRevisionPerfil tipo;

    @Column(name = "realizado_por", nullable = false)
    private String realizadoPor;

    @Column(length = 300)
    private String comentario;

    @Column(name = "fecha_evento", nullable = false, updatable = false)
    private LocalDateTime fechaEvento;

    @PrePersist
    protected void onCreate() {
        this.fechaEvento = LocalDateTime.now(Clock.systemUTC());
    }
}
