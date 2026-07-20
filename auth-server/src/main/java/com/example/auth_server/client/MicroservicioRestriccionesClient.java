package com.example.auth_server.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "microservicio-restricciones")
public interface MicroservicioRestriccionesClient {

    @GetMapping("/api/restricciones/usuarios/{usuarioId}/estado")
    Map<String, Boolean> verificarEstadoUsuario(@PathVariable("usuarioId") Long usuarioId);
}
