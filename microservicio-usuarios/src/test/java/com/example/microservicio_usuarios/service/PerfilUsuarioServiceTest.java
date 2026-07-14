package com.example.microservicio_usuarios.service;

import com.example.microservicio_usuarios.dto.ModeracionPerfilRequest;
import com.example.microservicio_usuarios.dto.PerfilUsuarioResponse;
import com.example.microservicio_usuarios.dto.PerfilUsuarioUpdateRequest;
import com.example.microservicio_usuarios.entity.EstadoModeracionPerfil;
import com.example.microservicio_usuarios.entity.PerfilUsuario;
import com.example.microservicio_usuarios.entity.Usuario;
import com.example.microservicio_usuarios.repository.PerfilUsuarioRepository;
import com.example.microservicio_usuarios.repository.RevisionPerfilRepository;
import com.example.microservicio_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilUsuarioServiceTest {

    @Mock
    private PerfilUsuarioRepository perfilUsuarioRepository;

    @Mock
    private RevisionPerfilRepository revisionPerfilRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private PerfilUsuarioService perfilUsuarioService;

    @BeforeEach
    void setUp() {
        perfilUsuarioService = new PerfilUsuarioService(perfilUsuarioRepository, revisionPerfilRepository, usuarioRepository);
    }

    @Test
    void actualizarPerfilComoPropietarioLoMarcaPendiente() {
        Long userId = 2L;
        Usuario usuario = usuario(userId, "usuario1", "u1@mail.com", "Usuario Uno");
        PerfilUsuario perfil = perfil(userId, EstadoModeracionPerfil.APROBADO, false, false);

        PerfilUsuarioUpdateRequest request = new PerfilUsuarioUpdateRequest();
        request.setFotoPerfil("AVATAR_3");
        request.setNombrePerfil("Usuario Uno Editado");
        request.setDescripcionPersonal("Me gusta Spring");
        request.setEmailPrivado(true);
        request.setNombrePrivado(false);

        Jwt jwt = jwt("usuario1", userId, List.of("USER"));

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));
        when(perfilUsuarioRepository.findById(userId)).thenReturn(Optional.of(perfil));
        when(perfilUsuarioRepository.save(any(PerfilUsuario.class))).thenAnswer(inv -> inv.getArgument(0));

        PerfilUsuarioResponse response = perfilUsuarioService.actualizarPerfil(userId, request, jwt);

        assertThat(response.getEstadoModeracion()).isEqualTo(EstadoModeracionPerfil.PENDIENTE);
        assertThat(response.getNombrePerfil()).isEqualTo("Usuario Uno Editado");
        assertThat(response.isEmailPrivado()).isTrue();
        verify(revisionPerfilRepository).save(any());
    }

    @Test
    void obtenerPerfilDeTerceroOcultaDatosPrivados() {
        Long ownerId = 2L;
        Usuario usuario = usuario(ownerId, "usuario1", "u1@mail.com", "Usuario Uno");
        PerfilUsuario perfil = perfil(ownerId, EstadoModeracionPerfil.APROBADO, true, true);
        perfil.setNombrePerfil("Nombre Privado");
        perfil.setDescripcionPersonal("Descripcion visible si aprobado");

        Jwt tercero = jwt("usuario2", 3L, List.of("USER"));

        when(usuarioRepository.findById(ownerId)).thenReturn(Optional.of(usuario));
        when(perfilUsuarioRepository.findById(ownerId)).thenReturn(Optional.of(perfil));

        PerfilUsuarioResponse response = perfilUsuarioService.obtenerPerfil(ownerId, tercero);

        assertThat(response.getNombrePerfil()).isNull();
        assertThat(response.getEmail()).isNull();
        assertThat(response.getDescripcionPersonal()).isEqualTo("Descripcion visible si aprobado");
    }

    @Test
    void actualizarPerfilConContenidoInapropiadoBloqueaCambio() {
        Long userId = 2L;
        Usuario usuario = usuario(userId, "usuario1", "u1@mail.com", "Usuario Uno");
        PerfilUsuario perfil = perfil(userId, EstadoModeracionPerfil.APROBADO, false, false);

        PerfilUsuarioUpdateRequest request = new PerfilUsuarioUpdateRequest();
        request.setFotoPerfil("AVATAR_2");
        request.setNombrePerfil("Nombre limpio");
        request.setDescripcionPersonal("Esto es mierda");

        Jwt jwt = jwt("usuario1", userId, List.of("USER"));

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));
        when(perfilUsuarioRepository.findById(userId)).thenReturn(Optional.of(perfil));

        assertThatThrownBy(() -> perfilUsuarioService.actualizarPerfil(userId, request, jwt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no permitidas");

        verify(perfilUsuarioRepository, never()).save(ArgumentMatchers.any(PerfilUsuario.class));
        verify(revisionPerfilRepository).save(any());
    }

    @Test
    void moderacionAdminApruebaPerfil() {
        Long userId = 2L;
        Usuario usuario = usuario(userId, "usuario1", "u1@mail.com", "Usuario Uno");
        PerfilUsuario perfil = perfil(userId, EstadoModeracionPerfil.PENDIENTE, false, false);

        ModeracionPerfilRequest request = new ModeracionPerfilRequest();
        request.setAprobado(true);
        request.setComentario("Perfil correcto");

        Jwt admin = jwt("admin", 1L, List.of("ADMIN"));

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));
        when(perfilUsuarioRepository.findById(userId)).thenReturn(Optional.of(perfil));
        when(perfilUsuarioRepository.save(any(PerfilUsuario.class))).thenAnswer(inv -> inv.getArgument(0));

        PerfilUsuarioResponse response = perfilUsuarioService.moderarPerfil(userId, request, admin);

        assertThat(response.getEstadoModeracion()).isEqualTo(EstadoModeracionPerfil.APROBADO);
        assertThat(response.getObservacionModeracion()).isEqualTo("Perfil correcto");
        verify(revisionPerfilRepository).save(any());
    }

    private Usuario usuario(Long id, String username, String email, String nombre) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setNombre(nombre);
        usuario.setFechaRegistro(LocalDateTime.now());
        return usuario;
    }

    private PerfilUsuario perfil(Long userId, EstadoModeracionPerfil estado, boolean emailPrivado, boolean nombrePrivado) {
        PerfilUsuario perfil = new PerfilUsuario();
        perfil.setUsuarioId(userId);
        perfil.setFotoPerfil("AVATAR_1");
        perfil.setNombrePerfil("Nombre Base");
        perfil.setDescripcionPersonal("Descripcion base");
        perfil.setEmailPrivado(emailPrivado);
        perfil.setNombrePrivado(nombrePrivado);
        perfil.setEstadoModeracion(estado);
        perfil.setObservacionModeracion("ok");
        perfil.setUltimaActualizacion(LocalDateTime.now());
        return perfil;
    }

    private Jwt jwt(String subject, Long userId, List<String> roles) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("userId", userId)
                .claim("roles", roles)
                .subject(subject)
                .build();
    }
}
