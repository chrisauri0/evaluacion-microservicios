package com.example.microservicio_usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudAmistadResponse {
    private Long idSolicitud;
    private Long emisorId;
    private String emisorUsername;
    private String estado;
    private LocalDateTime fechaCreacion;
}