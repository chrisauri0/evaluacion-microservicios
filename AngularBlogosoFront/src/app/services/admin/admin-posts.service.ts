import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { forkJoin, map, Observable } from 'rxjs';
import { Post, PostCategory } from '../../models/post-card/post-card-model';
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

interface PostFormValue {
  category?: PostCategory | null;
  content?: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class AdminPostsService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly usuariosLookup = inject(UsuariosLookupService);
  private readonly gatewayUrl = 'http://localhost:8080/api';

  //CRUD

  getAllPosts(): Observable<Post[]> {
    return forkJoin({
      posts: this.http.get<BackendPost[]>(`${this.gatewayUrl}/posts`),
      users: this.usuariosLookup.getLookupMap(),
    }).pipe(map(({ posts, users }) => posts.map((p) => this.toPost(p, users))));
  }

  createPost(post: PostFormValue): Observable<Post> {
    const body = {
      titulo: (post.content ?? '').slice(0, 50),
      contenido: post.content ?? '',
      autorId: this.authService.getUserId(),
      categoria: post.category,
    };
    return forkJoin({
      created: this.http.post<BackendPost>(`${this.gatewayUrl}/posts`, body),
      users: this.usuariosLookup.getLookupMap(),
    }).pipe(map(({ created, users }) => this.toPost(created, users)));
  }

  updatePost(id: number, post: PostFormValue): Observable<Post> {
    const body = {
      titulo: (post.content ?? '').slice(0, 50),
      contenido: post.content ?? '',
      autorId: this.authService.getUserId(),
      categoria: post.category,
    };
    return forkJoin({
      updated: this.http.put<BackendPost>(`${this.gatewayUrl}/posts/${id}`, body),
      users: this.usuariosLookup.getLookupMap(),
    }).pipe(map(({ updated, users }) => this.toPost(updated, users)));
  }

  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${this.gatewayUrl}/posts/${id}`);
  }

  // RESTRICCIÓN

  restringirPost(postId: number, razon: string): Observable<any> {
    return this.http.post(`${this.gatewayUrl}/restricciones/post`, { postId, razon });
  }

  private toPost(p: BackendPost, users: Map<number, UsuarioLookup>): Post {
    const user = users.get(p.autorId);
    return {
      id: String(p.id),
      authorId: String(p.autorId),
      authorName: user?.displayName ?? `Usuario ${p.autorId}`,
      authorAvatar: user?.avatarIcon ?? '👤',
      createdAt: new Date(p.fechaCreacion),
      category: p.categoria as PostCategory,
      content: p.contenido,
      status: 'approved',
      likes: 0,
      reports: 0,
      comments: [],
    };
  }
}
