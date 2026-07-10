package com.example.microservicio_comentarios.repository;


import com.example.microservicio_comentarios.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByPostId(Long postId);
    List<Comentario> findByUsuarioId(Long usuarioId);
}