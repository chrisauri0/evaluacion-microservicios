package com.example.microservicio_restricciones.service;


import com.example.microservicio_restricciones.dto.BanearUsuarioRequest;
import com.example.microservicio_restricciones.dto.RestringirPostRequest;
import com.example.microservicio_restricciones.model.PublicacionRestringida;
import com.example.microservicio_restricciones.model.UsuarioBaneado;
import com.example.microservicio_restricciones.repository.PublicacionRestringidaRepository;
import com.example.microservicio_restricciones.repository.UsuarioBaneadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
public class RestriccionService {

    private static final Logger log = LoggerFactory.getLogger(RestriccionService.class);

    private final PublicacionRestringidaRepository publicacionRepo;
    private final UsuarioBaneadoRepository usuarioBaneadoRepo;

    public RestriccionService(PublicacionRestringidaRepository publicacionRepo,
                               UsuarioBaneadoRepository usuarioBaneadoRepo) {
        this.publicacionRepo = publicacionRepo;
        this.usuarioBaneadoRepo = usuarioBaneadoRepo;
    }

    // ---------- Restricción de publicaciones ----------

    public PublicacionRestringida restringirPost(RestringirPostRequest request) {
        if (publicacionRepo.existsByPostId(request.getPostId())) {
            throw new IllegalStateException("El post ya está restringido");
        }

        Long adminId = obtenerIdDelAdminActual();

        PublicacionRestringida restriccion = new PublicacionRestringida(
                request.getPostId(), request.getAutorId(), adminId, request.getMotivo()
        );
        PublicacionRestringida guardada = publicacionRepo.save(restriccion);

        notificarAutor(request.getAutorId(), request.getPostId(), request.getMotivo());

        return guardada;
    }

    public boolean estaRestringido(Long postId) {
        return publicacionRepo.existsByPostId(postId);
    }

    public void quitarRestriccion(Long postId) {
        if (!publicacionRepo.existsByPostId(postId)) {
            throw new IllegalStateException("El post no está restringido");
        }
        publicacionRepo.deleteByPostId(postId);
    }

    public List<PublicacionRestringida> listarPostsRestringidos() {
        return publicacionRepo.findAll();
    }

    // ---------- Baneo de usuarios ----------

    public UsuarioBaneado banearUsuario(BanearUsuarioRequest request) {
        if (usuarioBaneadoRepo.existsByUsuarioId(request.getUsuarioId())) {
            throw new IllegalStateException("El usuario ya está baneado");
        }

        Long adminId = obtenerIdDelAdminActual();

        UsuarioBaneado baneo = new UsuarioBaneado(
                request.getUsuarioId(), adminId, request.getMotivo()
        );
        return usuarioBaneadoRepo.save(baneo);
    }

    public boolean estaBaneado(Long usuarioId) {
        return usuarioBaneadoRepo.existsByUsuarioId(usuarioId);
    }

    public void quitarBaneo(Long usuarioId) {
        if (!usuarioBaneadoRepo.existsByUsuarioId(usuarioId)) {
            throw new IllegalStateException("El usuario no está baneado");
        }
        usuarioBaneadoRepo.deleteByUsuarioId(usuarioId);
    }

    public List<UsuarioBaneado> listarUsuariosBaneados() {
        return usuarioBaneadoRepo.findAll();
    }

    // ---------- Helpers ----------

    private Long obtenerIdDelAdminActual() {
        // El JWT trae el "sub" (subject) como identificador del usuario autenticado
        var auth = SecurityContextHolder.getContext().getAuthentication();
        // Aquí asumimos que el subject del JWT es el ID del admin; ajusta según tu JWT real
        return Long.parseLong(auth.getName());
    }

    private void notificarAutor(Long autorId, Long postId, String motivo) {
        // TODO: reemplazar con una llamada real a un microservicio de notificaciones
        // o insertar en una tabla de notificaciones cuando ese servicio exista.
        log.info("NOTIFICACIÓN → Usuario {}: tu publicación {} fue restringida. Motivo: {}",
                autorId, postId, motivo);
    }
}