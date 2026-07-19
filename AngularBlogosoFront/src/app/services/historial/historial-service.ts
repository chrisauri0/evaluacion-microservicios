import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PostBackend } from '../../models/post-card/post-card-model';

@Injectable({ providedIn: 'root' })
export class HistorialService {
  private http = inject(HttpClient);
  private gatewayUrl = 'http://localhost:8080/api/posts';

  misPublicaciones(filtros?: { categoria?: string; desde?: string; hasta?: string }): Observable<PostBackend[]> {
    let params = new HttpParams();
    if (filtros?.categoria) params = params.set('categoria', filtros.categoria);
    if (filtros?.desde) params = params.set('desde', filtros.desde);
    if (filtros?.hasta) params = params.set('hasta', filtros.hasta);

    return this.http.get<PostBackend[]>(`${this.gatewayUrl}/mis-publicaciones`, { params });
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.gatewayUrl}/${id}`);
  }
}