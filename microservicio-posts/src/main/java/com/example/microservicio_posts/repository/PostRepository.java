package com.example.microservicio_posts.repository;


import com.example.microservicio_posts.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAutorId(Long autorId);
    List<Post> findByCategoria(String categoria);
}