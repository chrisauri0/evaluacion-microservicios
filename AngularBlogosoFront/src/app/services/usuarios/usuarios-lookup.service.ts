import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, shareReplay } from 'rxjs';

interface BackendUsuario {
  id: number;
  username: string;
  email: string;
  nombre: string;
}

const USUARIOS_URL = 'http://localhost:8080/api/usuarios/disponibles';

@Injectable({ providedIn: 'root' })
export class UsuariosLookupService {
  private readonly http = inject(HttpClient);
  private cache$: Observable<Map<number, string>> | null = null;

  getUsernameMap(): Observable<Map<number, string>> {
    if (!this.cache$) {
      this.cache$ = this.http.get<BackendUsuario[]>(USUARIOS_URL).pipe(
        map((usuarios) => new Map(usuarios.map((u) => [u.id, u.username]))),
        shareReplay(1),
      );
    }
    return this.cache$;
  }
}
