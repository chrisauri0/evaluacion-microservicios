import { Component, inject, signal, OnInit } from '@angular/core';
import { AmigosService } from '../../services/amigos/amigos-services';
import { SolicitudAmistadDto, UsuarioAmigo } from '../../models/amigos/amigos-model';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-amigos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './amigos.html',
})
export class AmigosComponent implements OnInit {
  amigosService = inject(AmigosService);
  authService = inject(AuthService);

  solicitudesPendientes = signal<SolicitudAmistadDto[]>([]);
  misAmigos = signal<UsuarioAmigo[]>([]);

  searchUsername = signal('');
  usuarioEncontrado = signal<any | null>(null);

  ngOnInit() {
    this.cargarSolicitudes();
    this.cargarAmigos();
  }

  cargarAmigos() {
    const currentUserId = this.authService.getUserId();
    if (!currentUserId) return;

    this.amigosService.obtenerIdsDeAmigos(currentUserId).subscribe({
      next: (ids) => {
        if (ids.length === 0) {
          this.misAmigos.set([]);
          return;
        }

        let amigosCargados: UsuarioAmigo[] = [];
        
        ids.forEach(id => {
          this.amigosService.obtenerUsuarioPorId(id).subscribe(usuario => {
            const amigo: UsuarioAmigo = {
              usuarioId: usuario.id,
              username: usuario.username,
              nombrePerfil: usuario.nombrePerfil || null,
              fotoPerfil: usuario.fotoPerfil || ''
            };
            amigosCargados.push(amigo);
            
            this.misAmigos.set([...amigosCargados]);
          });
        });
      },
      error: (e) => console.error('Error obteniendo IDs de amigos', e)
    });
  }

  cargarSolicitudes() {
    const currentUserId = this.authService.getUserId();
    if (!currentUserId) return;

    this.amigosService.listarPendientes(currentUserId).subscribe({
      next: (solicitudes) => {
        // Por cada solicitud, vamos a buscar el nombre del emisor
        solicitudes.forEach(sol => {
          this.amigosService.obtenerUsuarioPorId(sol.emisorId).subscribe(usuario => {
            sol.emisorNombre = usuario.username;
          });
        });
        this.solicitudesPendientes.set(solicitudes);
      },
      error: (e) => console.error('Error obteniendo solicitudes', e)
    });
  }

  buscarUsuario() {
    const username = this.searchUsername().trim();
    if (!username) return;

    this.amigosService.buscarPorUsername(username).subscribe({
      next: (usuario) => this.usuarioEncontrado.set(usuario),
      error: () => {
        alert('Usuario no encontrado');
        this.usuarioEncontrado.set(null);
      }
    });
  }

  enviarSolicitud(receptorId: number): void {
    const currentUserId = this.authService.getUserId();
    if (!currentUserId) {
      alert('Necesitas iniciar sesión para enviar solicitudes.');
      return;
    }

    const dto: SolicitudAmistadDto = { emisorId: currentUserId, receptorId: receptorId };
    this.amigosService.enviar(dto).subscribe({
      next: () => alert('Solicitud enviada exitosamente'),
      error: (e) => console.error('Error al enviar', e)
    });
  }

  responderSolicitud(solicitudId: number | undefined, estado: string): void {
    if (!solicitudId) return;
    
    this.amigosService.actualizarEstado(solicitudId, estado).subscribe({
      next: () => {
        this.cargarSolicitudes();
	this.cargarAmigos();
        alert(`Solicitud ${estado.toLowerCase()}`);
      },
      error: (e) => console.error('Error al responder', e)
    });
  }
}
