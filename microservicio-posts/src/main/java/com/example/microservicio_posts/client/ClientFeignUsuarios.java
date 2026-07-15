package com.example.microservicio_posts.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "microservicio-usuarios")
public interface ClientFeignUsuarios {

    @GetMapping("/api/usuarios/solicitudes/amigos/{userId}")
    public List<Long> obtenerAmigosIds(@PathVariable("userId") Long userId);
    
}