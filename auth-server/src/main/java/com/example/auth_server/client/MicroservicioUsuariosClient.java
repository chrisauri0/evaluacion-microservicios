package com.example.auth_server.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.example.auth_server.dto.UsuarioProfileRequest;

@FeignClient(name = "microservicio-usuarios")
public interface MicroservicioUsuariosClient {
    
    @PostMapping("/api/usuarios")
    void crearUsuarioProfile(@RequestBody UsuarioProfileRequest request);

    @PutMapping("/api/usuarios/{id}")
    void actualizarUsuarioProfile(@PathVariable("id") Long id, @RequestBody UsuarioProfileRequest request);

    @DeleteMapping("/api/usuarios/{id}")
    void eliminarUsuarioProfile(@PathVariable("id") Long id);
}