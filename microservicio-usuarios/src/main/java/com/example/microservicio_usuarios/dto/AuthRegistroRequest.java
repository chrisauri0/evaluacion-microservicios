package com.example.microservicio_usuarios.dto;

import lombok.Data;

@Data
public class AuthRegistroRequest {
    private String username;
    private String password;
    private String rol;
    private String email;
    private String nombre;
}
