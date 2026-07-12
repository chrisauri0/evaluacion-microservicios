package com.example.microservicio_usuarios.config;

import com.example.microservicio_usuarios.entity.Usuario;
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
                crearUsuario(usuarioRepository, 1L, "admin", "admin@escuela.com", "Administrador");
                crearUsuario(usuarioRepository, 2L, "usuario1", "usuario1@escuela.com", "Usuario Uno");
                crearUsuario(usuarioRepository, 3L, "usuario2", "usuario2@escuela.com", "Usuario Dos");
                crearUsuario(usuarioRepository, 4L, "usuario3", "usuario3@escuela.com", "Usuario Tres");

                System.out.println("Perfiles de usuario sembrados (ids 1-4)");
            }
        };
    }

    private void crearUsuario(UsuarioRepository repo, Long id, String user, String email, String nombre) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setUsername(user);
        u.setEmail(email);
        u.setNombre(nombre);
        repo.save(u);
    }
}