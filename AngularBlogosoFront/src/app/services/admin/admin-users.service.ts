import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { forkJoin, map, Observable, tap } from 'rxjs';
import { UsuariosLookupService } from '../usuarios/usuarios-lookup.service';

export interface UsuarioDto {
  id: number;
  username: string;
  email: string;
  nombre: string;
  fechaRegistro?: string;
  rol?: string;       // Agregado para el HTML
  avatarUrl?: string;
  avatarIcon?: string;
}

export interface RegistroRequest {
  username: string;
  email: string;
  nombre: string;
  password?: string;
  rol: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminUsersService {
  private readonly http = inject(HttpClient);
  private readonly usuariosLookup = inject(UsuariosLookupService);
  // Apuntamos al AUTH-SERVER a través del Gateway
  private readonly gatewayUrl = 'http://localhost:8080/api/auth/usuarios';

  getAllUsers(): Observable<UsuarioDto[]> {
    return forkJoin({
      users: this.http.get<UsuarioDto[]>(this.gatewayUrl),
      lookup: this.usuariosLookup.getLookupMap(true),
    }).pipe(
      map(({ users, lookup }) =>
        users.map((user) => {
          const userLookup = lookup.get(user.id);
          return {
            ...user,
            nombre: userLookup?.displayName ?? user.nombre,
            avatarIcon: userLookup?.avatarIcon ?? '👤',
            avatarUrl: user.avatarUrl,
          };
        }),
      ),
    );
  }

  getUserById(id: number): Observable<UsuarioDto> {
    return this.http.get<UsuarioDto>(`${this.gatewayUrl}/${id}`);
  }

  createUser(request: RegistroRequest): Observable<UsuarioDto> {
    return this.http.post<UsuarioDto>(this.gatewayUrl, request).pipe(
      tap(() => this.usuariosLookup.invalidateCache()),
    );
  }

  updateUser(id: number, request: Partial<RegistroRequest>): Observable<UsuarioDto> {
    return this.http.put<UsuarioDto>(`${this.gatewayUrl}/${id}`, request).pipe(
      tap(() => this.usuariosLookup.invalidateCache()),
    );
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.gatewayUrl}/${id}`).pipe(
      tap(() => this.usuariosLookup.invalidateCache()),
    );
  }
}
