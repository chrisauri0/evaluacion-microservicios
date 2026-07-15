package com.example.microservicio_posts.repository;

import com.example.microservicio_posts.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    List<Publicacion> findByAutorId(Long autorId);
    List<Publicacion> findByCategoria(String categoria);
}
