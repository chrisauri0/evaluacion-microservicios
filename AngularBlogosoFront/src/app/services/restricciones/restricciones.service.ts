import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

interface PublicacionRestringida {
  id: number;
  postId: number;
  autorId: number;
  adminId: number;
  motivo: string;
  fechaRestriccion: string;
}

const RESTRICCIONES_URL = 'http://localhost:8080/api/restricciones/posts';

@Injectable({ providedIn: 'root' })
export class RestriccionesService {
  private readonly http = inject(HttpClient);

  listarPostsRestringidos(): Observable<Set<number>> {
    return this.http
      .get<PublicacionRestringida[]>(RESTRICCIONES_URL)
      .pipe(map((lista) => new Set(lista.map((r) => r.postId))));
  }

  restringir(postId: number, autorId: number, motivo: string): Observable<PublicacionRestringida> {
    return this.http.post<PublicacionRestringida>(RESTRICCIONES_URL, { postId, autorId, motivo });
  }

  quitarRestriccion(postId: number): Observable<void> {
    return this.http.delete<void>(`${RESTRICCIONES_URL}/${postId}`);
  }
}
