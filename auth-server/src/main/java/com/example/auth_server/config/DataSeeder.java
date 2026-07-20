package com.example.auth_server.config;

import com.example.auth_server.entity.Usuario;
import com.example.auth_server.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedUsuarios(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                Usuario admin = new Usuario();
                admin.setId(1L);
                admin.setUsername("admin");
		admin.setNombre("Administrador");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol("ADMIN");
        		admin.setEmail("admin@escuela.com");
		admin.setFechaRegistro(LocalDate.now());
                usuarioRepository.save(admin);

                for (int i = 1; i <= 3; i++) {
                    Usuario usuario = new Usuario();
                    usuario.setId((long) (i + 1));
                    usuario.setUsername("usuario" + i);
		    usuario.setNombre("Usuario " + i);
                    usuario.setPassword(passwordEncoder.encode("user123"));
                    usuario.setRol("USER");
		    usuario.setEmail("usuario" + i + "@escuela.com");
		    usuario.setFechaRegistro(LocalDate.now());
		    usuarioRepository.save(usuario);
                }
                System.out.println("Usuarios sembrados: admin (id=1), usuario1 (id=2), usuario2 (id=3), usuario3 (id=4)");
            }
        };
    }

    
}   
