package com.example.microservicio_usuarios.service;

import com.example.microservicio_usuarios.dto.ModeracionPerfilRequest;
import com.example.microservicio_usuarios.dto.PerfilUsuarioResponse;
import com.example.microservicio_usuarios.dto.PerfilUsuarioUpdateRequest;
import com.example.microservicio_usuarios.entity.EstadoModeracionPerfil;
import com.example.microservicio_usuarios.entity.PerfilUsuario;
import com.example.microservicio_usuarios.entity.RevisionPerfil;
import com.example.microservicio_usuarios.entity.TipoRevisionPerfil;
import com.example.microservicio_usuarios.entity.Usuario;
import com.example.microservicio_usuarios.repository.PerfilUsuarioRepository;
import com.example.microservicio_usuarios.repository.RevisionPerfilRepository;
import com.example.microservicio_usuarios.repository.UsuarioRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class PerfilUsuarioService {

    private static final Set<String> PALABRAS_PROHIBIDAS = Set.of(
            "idiota", "estupido", "estupida", "mierda", "imbecil"
    );

    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final RevisionPerfilRepository revisionPerfilRepository;
    private final UsuarioRepository usuarioRepository;

    public PerfilUsuarioService(
            PerfilUsuarioRepository perfilUsuarioRepository,
            RevisionPerfilRepository revisionPerfilRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.perfilUsuarioRepository = perfilUsuarioRepository;
        this.revisionPerfilRepository = revisionPerfilRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public PerfilUsuarioResponse actualizarPerfil(Long usuarioId, PerfilUsuarioUpdateRequest request, Jwt jwt) {
        validarPermisoPropietarioOAdmin(usuarioId, jwt);

        Usuario usuario = obtenerUsuario(usuarioId);
        PerfilUsuario perfil = obtenerOCrearPerfil(usuario);

        validarContenidoApropiado(request.getNombrePerfil(), request.getDescripcionPersonal(), usuarioId, jwt.getSubject());

        perfil.setFotoPerfil(request.getFotoPerfil());
        perfil.setNombrePerfil(request.getNombrePerfil());
        perfil.setDescripcionPersonal(request.getDescripcionPersonal());
        perfil.setEmailPrivado(request.isEmailPrivado());
        perfil.setNombrePrivado(request.isNombrePrivado());
        perfil.setEstadoModeracion(EstadoModeracionPerfil.PENDIENTE);
        perfil.setObservacionModeracion("Pendiente de revision de administrador");

        PerfilUsuario perfilGuardado = perfilUsuarioRepository.save(perfil);
        registrarRevision(usuarioId, TipoRevisionPerfil.ACTUALIZACION_USUARIO, jwt.getSubject(), "Solicitud de actualizacion de perfil");

        return toResponse(perfilGuardado, usuario, true);
    }

    @Transactional(readOnly = true)
    public PerfilUsuarioResponse obtenerPerfil(Long usuarioId, Jwt jwt) {
        Usuario usuario = obtenerUsuario(usuarioId);
        PerfilUsuario perfil = obtenerOCrearPerfil(usuario);

        boolean esAdmin = esAdmin(jwt);
        boolean esPropietario = esPropietario(usuarioId, jwt);

        return toResponse(perfil, usuario, esAdmin || esPropietario);
    }

    @Transactional(readOnly = true)
    public List<PerfilUsuarioResponse> listarPendientes(Jwt jwt) {
        validarAdmin(jwt);

        return perfilUsuarioRepository
                .findByEstadoModeracionOrderByUltimaActualizacionDesc(EstadoModeracionPerfil.PENDIENTE)
                .stream()
                .map(perfil -> {
                    Usuario usuario = obtenerUsuario(perfil.getUsuarioId());
                    return toResponse(perfil, usuario, true);
                })
                .toList();
    }

    @Transactional
    public PerfilUsuarioResponse moderarPerfil(Long usuarioId, ModeracionPerfilRequest request, Jwt jwt) {
        validarAdmin(jwt);

        Usuario usuario = obtenerUsuario(usuarioId);
        PerfilUsuario perfil = obtenerOCrearPerfil(usuario);

        boolean aprobado = Boolean.TRUE.equals(request.getAprobado());
        if (aprobado) {
            perfil.setEstadoModeracion(EstadoModeracionPerfil.APROBADO);
            perfil.setObservacionModeracion(request.getComentario());
            registrarRevision(usuarioId, TipoRevisionPerfil.APROBACION_ADMIN, jwt.getSubject(), request.getComentario());
        } else {
            perfil.setEstadoModeracion(EstadoModeracionPerfil.RECHAZADO);
            perfil.setObservacionModeracion(request.getComentario());
            registrarRevision(usuarioId, TipoRevisionPerfil.RECHAZO_ADMIN, jwt.getSubject(), request.getComentario());
        }

        PerfilUsuario perfilGuardado = perfilUsuarioRepository.save(perfil);
        return toResponse(perfilGuardado, usuario, true);
    }

    @Transactional
    public void eliminarPerfilPorUsuarioId(Long usuarioId) {
        perfilUsuarioRepository.deleteById(usuarioId);
    }

    private Usuario obtenerUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));
    }

    private PerfilUsuario obtenerOCrearPerfil(Usuario usuario) {
        return perfilUsuarioRepository.findById(usuario.getId())
                .orElseGet(() -> {
                    PerfilUsuario perfil = new PerfilUsuario();
                    perfil.setUsuarioId(usuario.getId());
                    perfil.setFotoPerfil("AVATAR_1");
                    perfil.setNombrePerfil(usuario.getNombre());
                    perfil.setDescripcionPersonal("");
                    perfil.setEmailPrivado(false);
                    perfil.setNombrePrivado(false);
                    perfil.setEstadoModeracion(EstadoModeracionPerfil.APROBADO);
                    perfil.setObservacionModeracion("Perfil inicial");
                    return perfilUsuarioRepository.save(perfil);
                });
    }

    private PerfilUsuarioResponse toResponse(PerfilUsuario perfil, Usuario usuario, boolean puedeVerPrivado) {
        boolean perfilAprobado = perfil.getEstadoModeracion() == EstadoModeracionPerfil.APROBADO;
        boolean mostrarNombre = puedeVerPrivado || (!perfil.isNombrePrivado() && perfilAprobado);
        boolean mostrarEmail = puedeVerPrivado || (!perfil.isEmailPrivado() && perfilAprobado);
        boolean mostrarDescripcion = puedeVerPrivado || perfilAprobado;

        return PerfilUsuarioResponse.builder()
                .usuarioId(usuario.getId())
                .username(usuario.getUsername())
                .nombrePerfil(mostrarNombre ? perfil.getNombrePerfil() : null)
                .email(mostrarEmail ? usuario.getEmail() : null)
                .descripcionPersonal(mostrarDescripcion ? perfil.getDescripcionPersonal() : null)
                .fotoPerfil(perfil.getFotoPerfil())
                .emailPrivado(perfil.isEmailPrivado())
                .nombrePrivado(perfil.isNombrePrivado())
                .estadoModeracion(perfil.getEstadoModeracion())
                .observacionModeracion(puedeVerPrivado ? perfil.getObservacionModeracion() : null)
                .ultimaActualizacion(perfil.getUltimaActualizacion())
                .build();
    }

    private void validarContenidoApropiado(String nombrePerfil, String descripcion, Long usuarioId, String actor) {
        String contenido = (nombrePerfil + " " + (descripcion == null ? "" : descripcion)).toLowerCase(Locale.ROOT);

        boolean contieneInapropiado = PALABRAS_PROHIBIDAS.stream().anyMatch(contenido::contains);
        if (contieneInapropiado) {
            registrarRevision(
                    usuarioId,
                    TipoRevisionPerfil.RECHAZO_AUTOMATICO,
                    actor,
                    "Actualizacion bloqueada por contenido inapropiado"
            );
            throw new IllegalArgumentException("El perfil contiene palabras no permitidas");
        }
    }

    private void registrarRevision(Long usuarioId, TipoRevisionPerfil tipo, String realizadoPor, String comentario) {
        RevisionPerfil revision = new RevisionPerfil();
        revision.setUsuarioId(usuarioId);
        revision.setTipo(tipo);
        revision.setRealizadoPor(realizadoPor);
        revision.setComentario(comentario);
        revisionPerfilRepository.save(revision);
    }

    private void validarPermisoPropietarioOAdmin(Long usuarioId, Jwt jwt) {
        if (!esPropietario(usuarioId, jwt) && !esAdmin(jwt)) {
            throw new IllegalStateException("No tienes permisos para modificar este perfil");
        }
    }

    private void validarAdmin(Jwt jwt) {
        if (!esAdmin(jwt)) {
            throw new IllegalStateException("Solo un administrador puede realizar esta operacion");
        }
    }

    private boolean esPropietario(Long usuarioId, Jwt jwt) {
        Number userIdToken = jwt.getClaim("userId");
        return userIdToken != null && userIdToken.longValue() == usuarioId;
    }

    private boolean esAdmin(Jwt jwt) {
        Collection<String> roles = jwt.getClaim("roles");
        return roles != null && roles.stream().anyMatch("ADMIN"::equalsIgnoreCase);
    }
}
