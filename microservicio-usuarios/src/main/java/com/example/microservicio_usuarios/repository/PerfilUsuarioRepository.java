package com.example.microservicio_usuarios.repository;

import com.example.microservicio_usuarios.entity.EstadoModeracionPerfil;
import com.example.microservicio_usuarios.entity.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerfilUsuarioRepository extends JpaRepository<PerfilUsuario, Long> {
    List<PerfilUsuario> findByEstadoModeracionOrderByUltimaActualizacionDesc(EstadoModeracionPerfil estadoModeracion);
}
