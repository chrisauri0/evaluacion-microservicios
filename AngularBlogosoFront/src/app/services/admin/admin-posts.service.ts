import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post } from '../../models/post-card/post-card-model'; 

@Injectable({
  providedIn: 'root'
})
export class AdminPostsService {
  private http = inject(HttpClient);
  private gatewayUrl = 'http://localhost:8080/api'; 

  //CRUD

  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.gatewayUrl}/posts`);
  }

  createPost(post: any): Observable<Post> {
    return this.http.post<Post>(`${this.gatewayUrl}/posts`, post);
  }

  updatePost(id: number, post: any): Observable<Post> {
    return this.http.put<Post>(`${this.gatewayUrl}/posts/${id}`, post);
  }

  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${this.gatewayUrl}/posts/${id}`);
  }

  // RESTRICCIÓN

  restringirPost(postId: number, razon: string): Observable<any> {
    return this.http.post(`${this.gatewayUrl}/restricciones/post`, { postId, razon });
  }
}
