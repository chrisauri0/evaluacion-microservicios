package com.example.microservicio_usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PerfilUsuarioUpdateRequest {

    @NotBlank(message = "La foto de perfil es obligatoria")
    @Pattern(
            regexp = "AVATAR_[1-8]",
            message = "La foto debe ser una opcion predefinida entre AVATAR_1 y AVATAR_8"
    )
    private String fotoPerfil;

    @NotBlank(message = "El nombre de perfil es obligatorio")
    @Size(max = 80, message = "El nombre de perfil no puede exceder 80 caracteres")
    private String nombrePerfil;

    @Size(max = 280, message = "La descripcion no puede exceder 280 caracteres")
    private String descripcionPersonal;

    private boolean emailPrivado;

    private boolean nombrePrivado;
}
