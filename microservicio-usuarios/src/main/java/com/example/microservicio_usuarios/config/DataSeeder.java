package com.example.microservicio_usuarios.config;

import com.example.microservicio_usuarios.model.Usuario;
import com.example.microservicio_usuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedUsuarios(UsuarioRepository usuarioRepository) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                usuarioRepository.save(new Usuario(1L, "admin", "admin@escuela.com", "Administrador"));
                usuarioRepository.save(new Usuario(2L, "usuario1", "usuario1@escuela.com", "Usuario Uno"));
                usuarioRepository.save(new Usuario(3L, "usuario2", "usuario2@escuela.com", "Usuario Dos"));
                usuarioRepository.save(new Usuario(4L, "usuario3", "usuario3@escuela.com", "Usuario Tres"));

                System.out.println("✅ Perfiles de usuario sembrados (ids 1-4)");
            }
        };
    }
}