package com.example.microservicio_usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.microservicio_usuarios.entity.SolicitudAmistad;

public interface SolicitudAmistadRepository extends JpaRepository<SolicitudAmistad, Long> {

}