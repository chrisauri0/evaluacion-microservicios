package com.example.microservicio_usuarios.dto;

import lombok.Data;

@Data
public class UsuarioRequest {
    private Long id;
    private String username;
    private String email;
    private String nombre;
}