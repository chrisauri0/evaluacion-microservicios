package com.example.auth_server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    private Long id;
    private String username;
    private String nombre;
    private String password;
    private String rol;
    private String email;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;
}
