import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { forkJoin } from 'rxjs';
import { Post, NewPost, Comment } from '../../models/post-card/post-card-model';
import { AuthService } from '../../auth/auth.service';
import { UsuariosLookupService, UsuarioLookup } from '../usuarios/usuarios-lookup.service';

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

  private readonly _posts = signal<Post[]>([]);
  posts = this._posts.asReadonly();

  constructor() {
    this.refresh();
  }

  refresh(): void {
    forkJoin({
      posts: this.http.get<BackendPost[]>(POSTS_URL),
      users: this.usuariosLookup.getLookupMap(),
    }).subscribe({
      next: ({ posts, users }) => this._posts.set(posts.map((p) => this.toPost(p, users))),
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
      users: this.usuariosLookup.getLookupMap(),
    }).subscribe({
      next: ({ created, users }) =>
        this._posts.update((posts) => [this.toPost(created, users), ...posts]),
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
      users: this.usuariosLookup.getLookupMap(),
    }).subscribe({
      next: ({ comentarios, users }) => {
        const comments = comentarios.map((c) => this.toComment(c, users));
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
      users: this.usuariosLookup.getLookupMap(),
    }).subscribe({
      next: ({ created, users }) => {
        const comment = this.toComment(created, users);
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

  private toPost(p: BackendPost, users: Map<number, UsuarioLookup>): Post {
    const user = users.get(p.autorId);
    return {
      id: String(p.id),
      authorId: String(p.autorId),
      authorName: user?.displayName ?? `Usuario ${p.autorId}`,
      authorAvatar: user?.avatarIcon ?? '👤',
      createdAt: new Date(p.fechaCreacion),
      category: p.categoria as Post['category'],
      content: p.contenido,
      status: 'approved',
      likes: 0,
      reports: 0,
      comments: [],
    };
  }

  private toComment(c: BackendComentario, users: Map<number, UsuarioLookup>): Comment {
    const user = users.get(c.usuarioId);
    return {
      id: String(c.id),
      postId: String(c.postId),
      authorName: user?.displayName ?? `Usuario ${c.usuarioId}`,
      authorAvatar: user?.avatarIcon ?? '👤',
      content: c.contenido,
      createdAt: new Date(c.fechaCreacion),
    };
  }
}
