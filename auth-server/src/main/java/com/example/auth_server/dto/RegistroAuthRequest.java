package com.example.auth_server.dto;

import lombok.Data;

@Data
public class RegistroAuthRequest {
    private String username;
    private String password;
    private String rol;
    private String email;
    private String nombre;
}
