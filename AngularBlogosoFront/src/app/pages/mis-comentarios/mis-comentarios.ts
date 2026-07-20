import { CommonModule, DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../auth/auth.service';
import { ComentarioBackend, ComentariosService } from '../../services/comentarios/comentarios-service';

type EditState = {
  id: number;
  contenido: string;
  postId: number;
};

@Component({
  selector: 'app-mis-comentarios',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './mis-comentarios.html',
})
export class MisComentariosComponent implements OnInit {
  private readonly comentariosService = inject(ComentariosService);
  private readonly authService = inject(AuthService);

  comentarios = signal<ComentarioBackend[]>([]);
  isLoading = signal(true);
  errorMsg = signal<string | null>(null);
  editState = signal<EditState | null>(null);
  isSaving = signal(false);

  ngOnInit(): void {
    this.cargarComentarios();
  }

  cargarComentarios(): void {
    this.isLoading.set(true);
    this.errorMsg.set(null);

    this.comentariosService.misComentarios().subscribe({
      next: (data) => {
        this.comentarios.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMsg.set('No se pudieron cargar tus comentarios.');
        this.isLoading.set(false);
      },
    });
  }

  iniciarEdicion(comment: ComentarioBackend): void {
    this.editState.set({
      id: comment.id,
      contenido: comment.contenido,
      postId: comment.postId,
    });
  }

  cancelarEdicion(): void {
    this.editState.set(null);
  }

  actualizarContenidoEdicion(value: string): void {
    const state = this.editState();
    if (!state) {
      return;
    }

    this.editState.set({
      ...state,
      contenido: value,
    });
  }

  guardarEdicion(): void {
    const state = this.editState();
    const usuarioId = this.authService.getUserId();

    if (!state || usuarioId === null || this.isSaving()) {
      return;
    }

    const contenido = state.contenido.trim();
    if (!contenido) {
      this.errorMsg.set('El comentario no puede ir vacio.');
      return;
    }

    this.isSaving.set(true);
    this.comentariosService.editarComentario({
      id: state.id,
      contenido,
      postId: state.postId,
      usuarioId,
    }).subscribe({
      next: (updated) => {
        this.comentarios.update((current) => current.map((c) => (c.id === updated.id ? updated : c)));
        this.editState.set(null);
        this.isSaving.set(false);
      },
      error: () => {
        this.errorMsg.set('No se pudo editar el comentario.');
        this.isSaving.set(false);
      },
    });
  }

  eliminarComentario(commentId: number): void {
    if (!confirm('¿Eliminar este comentario?')) {
      return;
    }

    this.comentariosService.eliminarComentario(commentId).subscribe({
      next: () => this.comentarios.update((current) => current.filter((c) => c.id !== commentId)),
      error: () => this.errorMsg.set('No se pudo eliminar el comentario.'),
    });
  }
}
