package com.example.microservicio_usuarios.service;

import com.example.microservicio_usuarios.dto.SolicitudAmistadDto;
import com.example.microservicio_usuarios.entity.EstadoSolicitud;
import com.example.microservicio_usuarios.entity.SolicitudAmistad;
import com.example.microservicio_usuarios.repository.SolicitudAmistadRepository;
import com.example.microservicio_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SolicitudAmistadService {
    @Autowired
    private SolicitudAmistadRepository repository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public SolicitudAmistadDto create(SolicitudAmistadDto dto) {
        boolean existe = repository.findAll().stream()
            .anyMatch(s -> s.getEmisor().getId().equals(dto.getEmisorId()) 
                        && s.getReceptor().getId().equals(dto.getReceptorId())
                        && s.getEstado() == EstadoSolicitud.PENDIENTE);
        
        if (existe) {
            throw new IllegalStateException("Ya existe una solicitud pendiente entre estos usuarios");
        }

        SolicitudAmistad entity = toEntity(dto);
        return toDto(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<SolicitudAmistadDto> findByReceptor(Long receptorId) {
        return repository.findAll().stream()
            .filter(s -> s.getReceptor().getId().equals(receptorId) && s.getEstado() == EstadoSolicitud.PENDIENTE)
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public Optional<SolicitudAmistadDto> updateEstado(Long id, String estado) {
        return repository.findById(id).map(existing -> {
            existing.setEstado(EstadoSolicitud.valueOf(estado));
            return toDto(repository.save(existing));
        });
    }

    @Transactional(readOnly = true)
    public List<Long> obtenerAmigosIds(Long userId) {
        return repository.findAll().stream()
            .filter(s -> s.getEstado() == EstadoSolicitud.ACEPTADA)
            .filter(s -> s.getEmisor().getId().equals(userId) || s.getReceptor().getId().equals(userId))
            .map(s -> s.getEmisor().getId().equals(userId) ? s.getReceptor().getId() : s.getEmisor().getId())
            .collect(Collectors.toList());
    }

    // Converters
    private SolicitudAmistadDto toDto(SolicitudAmistad e) {
        SolicitudAmistadDto d = new SolicitudAmistadDto();
        d.setId(e.getId());
        d.setEmisorId(e.getEmisor().getId());
        d.setReceptorId(e.getReceptor().getId());
        d.setEstado(e.getEstado().name());
        d.setFechaCreacion(e.getFechaCreacion());
        return d;
    }

    private SolicitudAmistad toEntity(SolicitudAmistadDto d) {
        SolicitudAmistad e = new SolicitudAmistad();
        e.setEmisor(usuarioRepository.findById(d.getEmisorId()).orElseThrow());
        e.setReceptor(usuarioRepository.findById(d.getReceptorId()).orElseThrow());
        e.setEstado(EstadoSolicitud.PENDIENTE);
        return e;
    }
}