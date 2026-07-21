import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, shareReplay } from 'rxjs';

interface BackendUsuario {
  id: number;
  username: string;
  email: string;
  nombre: string;
  fotoPerfil?: string;
}

export interface UsuarioLookup {
  id: number;
  username: string;
  email: string;
  nombre: string;
  avatarCode: string;
  avatarIcon: string;
  displayName: string;
}

const USUARIOS_URL = 'http://localhost:8080/api/usuarios/disponibles';
const DEFAULT_AVATAR_CODE = 'AVATAR_1';

const AVATAR_ICON_BY_CODE: Record<string, string> = {
  AVATAR_1: '🦊',
  AVATAR_2: '🐱',
  AVATAR_3: '🐼',
  AVATAR_4: '🐸',
  AVATAR_5: '🦁',
  AVATAR_6: '🐧',
  AVATAR_7: '🐨',
  AVATAR_8: '🐹',
};

@Injectable({ providedIn: 'root' })
export class UsuariosLookupService {
  private readonly http = inject(HttpClient);
  private cache$: Observable<UsuarioLookup[]> | null = null;

  getUsuariosLookup(forceRefresh = false): Observable<UsuarioLookup[]> {
    if (forceRefresh) {
      this.invalidateCache();
    }

    this.cache$ ??= this.http.get<BackendUsuario[]>(USUARIOS_URL).pipe(
        map((usuarios) =>
          usuarios.map((u) => {
            const avatarCode = u.fotoPerfil || DEFAULT_AVATAR_CODE;
            return {
              id: u.id,
              username: u.username,
              email: u.email,
              nombre: u.nombre,
              avatarCode,
              avatarIcon: AVATAR_ICON_BY_CODE[avatarCode] ?? AVATAR_ICON_BY_CODE[DEFAULT_AVATAR_CODE],
              displayName: (u.nombre || u.username || `Usuario ${u.id}`).trim(),
            } satisfies UsuarioLookup;
          }),
        ),
        shareReplay(1),
      );

    return this.cache$;
  }

  getLookupMap(forceRefresh = false): Observable<Map<number, UsuarioLookup>> {
    return this.getUsuariosLookup(forceRefresh).pipe(
      map((usuarios) => new Map(usuarios.map((u) => [u.id, u]))),
    );
  }

  invalidateCache(): void {
    this.cache$ = null;
  }
}
