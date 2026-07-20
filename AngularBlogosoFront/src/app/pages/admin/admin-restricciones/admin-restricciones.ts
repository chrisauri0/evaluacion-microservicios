import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import {
  AdminRestriccionesService,
  PostAdminDto,
  PublicacionRestringidaDto,
  UsuarioAdminDto,
  UsuarioBaneadoDto,
} from '../../../services/admin/admin-restricciones.service';

@Component({
  selector: 'app-admin-restricciones',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-restricciones.html',
})
export class AdminRestriccionesComponent implements OnInit {
  private readonly service = inject(AdminRestriccionesService);

  isLoading = signal(true);
  error = signal<string | null>(null);

  posts = signal<PostAdminDto[]>([]);
  usuarios = signal<UsuarioAdminDto[]>([]);
  postsRestringidos = signal<PublicacionRestringidaDto[]>([]);
  usuariosBaneados = signal<UsuarioBaneadoDto[]>([]);

  selectedPostId = signal<number | null>(null);
  motivoPost = signal('');

  selectedUsuarioId = signal<number | null>(null);
  motivoUsuario = signal('');

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.isLoading.set(true);
    this.error.set(null);

    forkJoin({
      posts: this.service.getPosts(),
      usuarios: this.service.getUsuarios(),
      postsRestringidos: this.service.getPostsRestringidos(),
      usuariosBaneados: this.service.getUsuariosBaneados(),
    }).subscribe({
      next: ({ posts, usuarios, postsRestringidos, usuariosBaneados }) => {
        this.posts.set(posts);
        this.usuarios.set(usuarios);
        this.postsRestringidos.set(postsRestringidos);
        this.usuariosBaneados.set(usuariosBaneados);
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los datos de restricciones.');
        this.isLoading.set(false);
      },
    });
  }

  restringirPost(): void {
    const postId = this.selectedPostId();
    const motivo = this.motivoPost().trim();
    if (!postId || !motivo) {
      this.error.set('Debes seleccionar una publicacion y escribir un motivo.');
      return;
    }

    const post = this.posts().find((p) => p.id === postId);
    if (!post) {
      this.error.set('No se encontro la publicacion seleccionada.');
      return;
    }

    this.service.restringirPost(postId, post.autorId, motivo).subscribe({
      next: (nuevo) => {
        this.postsRestringidos.update((current) => [nuevo, ...current]);
        this.motivoPost.set('');
      },
      error: () => this.error.set('No se pudo restringir la publicacion.'),
    });
  }

  quitarRestriccion(postId: number): void {
    this.service.quitarRestriccionPost(postId).subscribe({
      next: () => this.postsRestringidos.update((current) => current.filter((p) => p.postId !== postId)),
      error: () => this.error.set('No se pudo quitar la restriccion del post.'),
    });
  }

  banearUsuario(): void {
    const usuarioId = this.selectedUsuarioId();
    const motivo = this.motivoUsuario().trim();
    if (!usuarioId || !motivo) {
      this.error.set('Debes seleccionar un usuario y escribir un motivo.');
      return;
    }

    this.service.banearUsuario(usuarioId, motivo).subscribe({
      next: (nuevo) => {
        this.usuariosBaneados.update((current) => [nuevo, ...current]);
        this.motivoUsuario.set('');
      },
      error: () => this.error.set('No se pudo banear al usuario.'),
    });
  }

  quitarBaneo(usuarioId: number): void {
    this.service.quitarBaneo(usuarioId).subscribe({
      next: () => this.usuariosBaneados.update((current) => current.filter((u) => u.usuarioId !== usuarioId)),
      error: () => this.error.set('No se pudo quitar el baneo del usuario.'),
    });
  }

  getNombreUsuario(usuarioId: number): string {
    return this.usuarios().find((u) => u.id === usuarioId)?.username ?? `Usuario #${usuarioId}`;
  }

  getTituloPost(postId: number): string {
    return this.posts().find((p) => p.id === postId)?.titulo ?? `Post #${postId}`;
  }
}
