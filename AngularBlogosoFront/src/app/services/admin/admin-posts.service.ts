import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { forkJoin, map, Observable } from 'rxjs';
import { Post, PostCategory } from '../../models/post-card/post-card-model';
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

interface PostFormValue {
  category?: PostCategory | null;
  content?: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class AdminPostsService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private usuariosLookup = inject(UsuariosLookupService);
  private restricciones = inject(RestriccionesService);
  private gatewayUrl = 'http://localhost:8080/api';

  //CRUD

  getAllPosts(): Observable<Post[]> {
    return forkJoin({
      posts: this.http.get<BackendPost[]>(`${this.gatewayUrl}/posts`),
      usernames: this.usuariosLookup.getUsernameMap(),
      restringidos: this.restricciones.listarPostsRestringidos(),
    }).pipe(
      map(({ posts, usernames, restringidos }) =>
        posts.map((p) => this.toPost(p, usernames, restringidos.has(p.id))),
      ),
    );
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
      usernames: this.usuariosLookup.getUsernameMap(),
    }).pipe(map(({ created, usernames }) => this.toPost(created, usernames, false)));
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
      usernames: this.usuariosLookup.getUsernameMap(),
    }).pipe(map(({ updated, usernames }) => this.toPost(updated, usernames, false)));
  }

  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${this.gatewayUrl}/posts/${id}`);
  }

  // RESTRICCIÓN

  restringirPost(postId: number, autorId: number, motivo: string): Observable<unknown> {
    return this.restricciones.restringir(postId, autorId, motivo);
  }

  quitarRestriccion(postId: number): Observable<void> {
    return this.restricciones.quitarRestriccion(postId);
  }

  private toPost(p: BackendPost, usernames: Map<number, string>, restringido: boolean): Post {
    return {
      id: String(p.id),
      authorId: String(p.autorId),
      authorName: usernames.get(p.autorId) ?? `Usuario ${p.autorId}`,
      authorAvatar: '👤',
      createdAt: new Date(p.fechaCreacion),
      category: p.categoria as PostCategory,
      content: p.contenido,
      status: restringido ? 'hidden' : 'approved',
      likes: 0,
      reports: 0,
      comments: [],
    };
  }
}
