package com.example.auth_server.dto;
import lombok.Data;

@Data
public class RegistroRequest {
    private String username;
    private String email;
    private String nombre;
    private String password;
    private String rol;
}