package com.example.microservicio_usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.microservicio_usuarios.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByEmail(String email);
}