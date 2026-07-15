package com.example.microservicio_usuarios.dto;

import com.example.microservicio_usuarios.entity.EstadoModeracionPerfil;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class PerfilUsuarioResponse {
    Long usuarioId;
    String username;
    String nombrePerfil;
    String email;
    String descripcionPersonal;
    String fotoPerfil;
    boolean emailPrivado;
    boolean nombrePrivado;
    EstadoModeracionPerfil estadoModeracion;
    String observacionModeracion;
    LocalDateTime ultimaActualizacion;
}
