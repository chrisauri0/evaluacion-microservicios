package com.example.microservicio_usuarios.client;

import com.example.microservicio_usuarios.dto.AuthRegistroRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "auth-server", path = "/api/auth-internal")
public interface AuthClient {
    
    @PostMapping("/register")
    Map<String, Object> registrarCredenciales(@RequestBody AuthRegistroRequest request);

    @PutMapping("/update/{id}")
    void actualizarCredenciales(@PathVariable("id") Long id, @RequestBody AuthRegistroRequest request);

    @DeleteMapping("/delete/{id}")
    void eliminarCredenciales(@PathVariable("id") Long id);

}
