import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PostAdminDto {
  id: number;
  titulo: string;
  contenido: string;
  autorId: number;
  categoria: string;
  fechaCreacion: string;
}

export interface UsuarioAdminDto {
  id: number;
  username: string;
  nombre: string;
  email: string;
  rol: string;
}

export interface PublicacionRestringidaDto {
  id: number;
  postId: number;
  autorId: number;
  adminId: number;
  motivo: string;
  fechaRestriccion: string;
}

export interface UsuarioBaneadoDto {
  id: number;
  usuarioId: number;
  adminId: number;
  motivo: string;
  fechaBaneo: string;
}

@Injectable({ providedIn: 'root' })
export class AdminRestriccionesService {
  private readonly http = inject(HttpClient);
  private readonly gatewayUrl = 'http://localhost:8080/api';

  getPosts(): Observable<PostAdminDto[]> {
    return this.http.get<PostAdminDto[]>(`${this.gatewayUrl}/posts`);
  }

  getUsuarios(): Observable<UsuarioAdminDto[]> {
    return this.http.get<UsuarioAdminDto[]>(`${this.gatewayUrl}/auth/usuarios`);
  }

  getPostsRestringidos(): Observable<PublicacionRestringidaDto[]> {
    return this.http.get<PublicacionRestringidaDto[]>(`${this.gatewayUrl}/restricciones/posts`);
  }

  restringirPost(postId: number, autorId: number, motivo: string): Observable<PublicacionRestringidaDto> {
    return this.http.post<PublicacionRestringidaDto>(`${this.gatewayUrl}/restricciones/posts`, {
      postId,
      autorId,
      motivo,
    });
  }

  quitarRestriccionPost(postId: number): Observable<void> {
    return this.http.delete<void>(`${this.gatewayUrl}/restricciones/posts/${postId}`);
  }

  getUsuariosBaneados(): Observable<UsuarioBaneadoDto[]> {
    return this.http.get<UsuarioBaneadoDto[]>(`${this.gatewayUrl}/restricciones/usuarios/baneados`);
  }

  banearUsuario(usuarioId: number, motivo: string): Observable<UsuarioBaneadoDto> {
    return this.http.post<UsuarioBaneadoDto>(`${this.gatewayUrl}/restricciones/usuarios/ban`, {
      usuarioId,
      motivo,
    });
  }

  quitarBaneo(usuarioId: number): Observable<void> {
    return this.http.delete<void>(`${this.gatewayUrl}/restricciones/usuarios/${usuarioId}/ban`);
  }
}
