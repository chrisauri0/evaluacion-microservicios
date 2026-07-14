package com.example.microservicio_usuarios.repository;

import com.example.microservicio_usuarios.entity.RevisionPerfil;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevisionPerfilRepository extends JpaRepository<RevisionPerfil, Long> {
}
