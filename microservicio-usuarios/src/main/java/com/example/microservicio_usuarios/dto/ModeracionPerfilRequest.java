package com.example.microservicio_usuarios.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ModeracionPerfilRequest {

    @NotNull(message = "Debe indicar si se aprueba o rechaza")
    private Boolean aprobado;

    @Size(max = 300, message = "El comentario no puede exceder 300 caracteres")
    private String comentario;
}
