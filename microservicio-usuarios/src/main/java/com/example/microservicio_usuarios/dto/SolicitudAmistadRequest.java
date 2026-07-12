package com.example.microservicio_usuarios.dto;

import lombok.Data;

@Data
public class SolicitudAmistadRequest {
    private Long emisorId;
    private Long receptorId;
}