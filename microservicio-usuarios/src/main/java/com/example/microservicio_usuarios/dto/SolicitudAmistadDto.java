package com.example.microservicio_usuarios.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SolicitudAmistadDto {
    private Long id;
    private Long emisorId;
    private Long receptorId;
    private String estado;
    private LocalDateTime fechaCreacion;
}