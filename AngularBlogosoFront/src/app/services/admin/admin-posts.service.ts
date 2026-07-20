import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Post, PostCategory } from '../../models/post-card/post-card-model';
import { AuthService } from '../../auth/auth.service';

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
  private gatewayUrl = 'http://localhost:8080/api';

  //CRUD

  getAllPosts(): Observable<Post[]> {
    return this.http
      .get<BackendPost[]>(`${this.gatewayUrl}/posts`)
      .pipe(map((posts) => posts.map((p) => this.toPost(p))));
  }

  createPost(post: PostFormValue): Observable<Post> {
    const body = {
      titulo: (post.content ?? '').slice(0, 50),
      contenido: post.content ?? '',
      autorId: this.authService.getUserId(),
      categoria: post.category,
    };
    return this.http.post<BackendPost>(`${this.gatewayUrl}/posts`, body).pipe(map((p) => this.toPost(p)));
  }

  updatePost(id: number, post: PostFormValue): Observable<Post> {
    const body = {
      titulo: (post.content ?? '').slice(0, 50),
      contenido: post.content ?? '',
      autorId: this.authService.getUserId(),
      categoria: post.category,
    };
    return this.http.put<BackendPost>(`${this.gatewayUrl}/posts/${id}`, body).pipe(map((p) => this.toPost(p)));
  }

  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${this.gatewayUrl}/posts/${id}`);
  }

  // RESTRICCIÓN

  restringirPost(postId: number, razon: string): Observable<any> {
    return this.http.post(`${this.gatewayUrl}/restricciones/post`, { postId, razon });
  }

  private toPost(p: BackendPost): Post {
    return {
      id: String(p.id),
      authorId: String(p.autorId),
      authorName: `Usuario ${p.autorId}`,
      authorAvatar: '👤',
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
