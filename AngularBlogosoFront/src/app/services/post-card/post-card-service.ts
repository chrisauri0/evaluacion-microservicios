import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { forkJoin } from 'rxjs';
import { Post, NewPost, Comment } from '../../models/post-card/post-card-model';
import { AuthService } from '../../auth/auth.service';
import { UsuariosLookupService } from '../usuarios/usuarios-lookup.service';
import { RestriccionesService } from '../restricciones/restricciones.service';

interface BackendPost {
  id: number;
  titulo: string;
  contenido: string;
  autorId: number;
  categoria: string;
  fechaCreacion: string;
}

interface BackendComentario {
  id: number;
  contenido: string;
  postId: number;
  usuarioId: number;
  fechaCreacion: string;
}

const GATEWAY_URL = 'http://localhost:8080/api';
const POSTS_URL = `${GATEWAY_URL}/posts`;
const COMENTARIOS_URL = `${GATEWAY_URL}/comentarios`;

@Injectable({ providedIn: 'root' })
export class PostService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly usuariosLookup = inject(UsuariosLookupService);
  private readonly restricciones = inject(RestriccionesService);

  private readonly _posts = signal<Post[]>([]);
  posts = this._posts.asReadonly();

  constructor() {
    this.refresh();
  }

  refresh(): void {
    forkJoin({
      posts: this.http.get<BackendPost[]>(POSTS_URL),
      usernames: this.usuariosLookup.getUsernameMap(),
      restringidos: this.restricciones.listarPostsRestringidos(),
    }).subscribe({
      next: ({ posts, usernames, restringidos }) =>
        this._posts.set(posts.map((p) => this.toPost(p, usernames, restringidos.has(p.id)))),
      error: (err) => console.error('No se pudieron cargar los posts:', err),
    });
  }

  create(newPost: NewPost, author: { id: string; name: string; avatar: string }): void {
    const body = {
      titulo: newPost.content.slice(0, 50),
      contenido: newPost.content,
      autorId: this.authService.getUserId() ?? Number(author.id),
      categoria: newPost.category,
    };

    forkJoin({
      created: this.http.post<BackendPost>(POSTS_URL, body),
      usernames: this.usuariosLookup.getUsernameMap(),
    }).subscribe({
      next: ({ created, usernames }) =>
        this._posts.update((posts) => [this.toPost(created, usernames, false), ...posts]),
      error: (err) => console.error('No se pudo crear el post:', err),
    });
  }

  // El backend de posts todavía no expone likes/reports, así que esas dos
  // acciones son solo optimistas en memoria por ahora.
  like(id: string): void {
    this._posts.update((posts) => posts.map((p) => (p.id === id ? { ...p, likes: p.likes + 1 } : p)));
  }

  report(id: string): void {
    this._posts.update((posts) => posts.map((p) => (p.id === id ? { ...p, reports: p.reports + 1 } : p)));
  }

  loadComments(postId: string): void {
    forkJoin({
      comentarios: this.http.get<BackendComentario[]>(`${COMENTARIOS_URL}/post/${postId}`),
      usernames: this.usuariosLookup.getUsernameMap(),
    }).subscribe({
      next: ({ comentarios, usernames }) => {
        const comments = comentarios.map((c) => this.toComment(c, usernames));
        this._posts.update((posts) => posts.map((p) => (p.id === postId ? { ...p, comments } : p)));
      },
      error: (err) => console.error('No se pudieron cargar los comentarios:', err),
    });
  }

  addComment(postId: string, content: string): void {
    const body = {
      contenido: content,
      postId: Number(postId),
      usuarioId: this.authService.getUserId(),
    };

    forkJoin({
      created: this.http.post<BackendComentario>(COMENTARIOS_URL, body),
      usernames: this.usuariosLookup.getUsernameMap(),
    }).subscribe({
      next: ({ created, usernames }) => {
        const comment = this.toComment(created, usernames);
        this._posts.update((posts) =>
          posts.map((p) => (p.id === postId ? { ...p, comments: [...p.comments, comment] } : p)),
        );
      },
      error: (err) => console.error('No se pudo publicar el comentario:', err),
    });
  }

  delete(id: string): void {
    this.http.delete<void>(`${POSTS_URL}/${id}`).subscribe({
      next: () => this._posts.update((posts) => posts.filter((p) => p.id !== id)),
      error: (err) => console.error('No se pudo borrar el post:', err),
    });
  }

  private toPost(p: BackendPost, usernames: Map<number, string>, restringido: boolean): Post {
    return {
      id: String(p.id),
      authorId: String(p.autorId),
      authorName: usernames.get(p.autorId) ?? `Usuario ${p.autorId}`,
      authorAvatar: '👤',
      createdAt: new Date(p.fechaCreacion),
      category: p.categoria as Post['category'],
      content: p.contenido,
      status: restringido ? 'hidden' : 'approved',
      likes: 0,
      reports: 0,
      comments: [],
    };
  }

  private toComment(c: BackendComentario, usernames: Map<number, string>): Comment {
    return {
      id: String(c.id),
      postId: String(c.postId),
      authorName: usernames.get(c.usuarioId) ?? `Usuario ${c.usuarioId}`,
      authorAvatar: '👤',
      content: c.contenido,
      createdAt: new Date(c.fechaCreacion),
    };
  }
}
