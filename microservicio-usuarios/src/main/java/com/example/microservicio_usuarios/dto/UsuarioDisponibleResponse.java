package com.example.microservicio_usuarios.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UsuarioDisponibleResponse {
    Long id;
    String username;
    String email;
    String nombre;
    String fotoPerfil;
}
