package com.example.microservicio_restricciones.repository;

import com.example.microservicio_restricciones.model.UsuarioBaneado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioBaneadoRepository extends JpaRepository<UsuarioBaneado, Long> {
    Optional<UsuarioBaneado> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioId(Long usuarioId);
    void deleteByUsuarioId(Long usuarioId);
}