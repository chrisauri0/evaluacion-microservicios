package com.example.auth_server.dto;
import lombok.Data;

@Data
public class UsuarioProfileRequest {
    private Long id;
    private String username;
    private String email;
    private String nombre;
}