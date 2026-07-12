package com.example.microservicio_usuarios.controller;

import com.example.microservicio_usuarios.dto.SolicitudAmistadDto;
import com.example.microservicio_usuarios.service.SolicitudAmistadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudAmistadController {

    @Autowired
    private SolicitudAmistadService service;

    @PostMapping
    public ResponseEntity<SolicitudAmistadDto> enviar(@RequestBody SolicitudAmistadDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/pendientes/{receptorId}")
    public ResponseEntity<List<SolicitudAmistadDto>> listar(@PathVariable Long receptorId) {
        return ResponseEntity.ok(service.findByReceptor(receptorId));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<SolicitudAmistadDto> actualizar(@PathVariable Long id, @RequestParam String estado) {
        return service.updateEstado(id, estado)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}