package com.example.microservicio_restricciones.repository;


import com.example.microservicio_restricciones.model.PublicacionRestringida;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PublicacionRestringidaRepository extends JpaRepository<PublicacionRestringida, Long> {
    Optional<PublicacionRestringida> findByPostId(Long postId);
    boolean existsByPostId(Long postId);
    void deleteByPostId(Long postId);
}