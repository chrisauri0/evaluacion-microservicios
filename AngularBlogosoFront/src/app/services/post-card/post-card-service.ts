import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Post, NewPost, Comment } from '../../models/post-card/post-card-model';
import { AuthService } from '../../auth/auth.service';

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

  private readonly _posts = signal<Post[]>([]);
  posts = this._posts.asReadonly();

  constructor() {
    this.refresh();
  }

  refresh(): void {
    this.http.get<BackendPost[]>(POSTS_URL).subscribe({
      next: (backendPosts) => this._posts.set(backendPosts.map((p) => this.toPost(p))),
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

    this.http.post<BackendPost>(POSTS_URL, body).subscribe({
      next: (created) => this._posts.update((posts) => [this.toPost(created), ...posts]),
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
    this.http.get<BackendComentario[]>(`${COMENTARIOS_URL}/post/${postId}`).subscribe({
      next: (comentarios) => {
        const comments = comentarios.map((c) => this.toComment(c));
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

    this.http.post<BackendComentario>(COMENTARIOS_URL, body).subscribe({
      next: (created) => {
        const comment = this.toComment(created);
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

  private toPost(p: BackendPost): Post {
    return {
      id: String(p.id),
      authorId: String(p.autorId),
      authorName: `Usuario ${p.autorId}`,
      authorAvatar: '👤',
      createdAt: new Date(p.fechaCreacion),
      category: p.categoria as Post['category'],
      content: p.contenido,
      status: 'approved',
      likes: 0,
      reports: 0,
      comments: [],
    };
  }

  private toComment(c: BackendComentario): Comment {
    return {
      id: String(c.id),
      postId: String(c.postId),
      authorName: `Usuario ${c.usuarioId}`,
      authorAvatar: '👤',
      content: c.contenido,
      createdAt: new Date(c.fechaCreacion),
    };
  }
}
