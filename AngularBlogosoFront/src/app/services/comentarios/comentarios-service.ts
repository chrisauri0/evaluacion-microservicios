import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ComentarioBackend {
  id: number;
  contenido: string;
  postId: number;
  usuarioId: number;
  fechaCreacion: string;
}

interface ComentarioRequest {
  contenido: string;
  postId: number;
  usuarioId: number;
}

@Injectable({ providedIn: 'root' })
export class ComentariosService {
  private readonly http = inject(HttpClient);
  private readonly gatewayUrl = 'http://localhost:8080/api/comentarios';

  misComentarios(): Observable<ComentarioBackend[]> {
    return this.http.get<ComentarioBackend[]>(`${this.gatewayUrl}/mis-comentarios`);
  }

  editarComentario(payload: ComentarioRequest & { id: number }): Observable<ComentarioBackend> {
    const { id, ...body } = payload;
    return this.http.put<ComentarioBackend>(`${this.gatewayUrl}/${id}`, body);
  }

  eliminarComentario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.gatewayUrl}/${id}`);
  }
}
