import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ModeracionPerfilRequest,
  PerfilUsuarioResponse,
  PerfilUsuarioUpdateRequest,
} from '../../models/perfil/perfil-model';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private http = inject(HttpClient);
  private gatewayUrl = 'http://localhost:8080/api/usuarios';

  obtenerPerfil(usuarioId: number): Observable<PerfilUsuarioResponse> {
    return this.http.get<PerfilUsuarioResponse>(`${this.gatewayUrl}/${usuarioId}/perfil`);
  }

  actualizarPerfil(
    usuarioId: number,
    request: PerfilUsuarioUpdateRequest,
  ): Observable<PerfilUsuarioResponse> {
    return this.http.put<PerfilUsuarioResponse>(`${this.gatewayUrl}/${usuarioId}/perfil`, request);
  }

  listarPendientesModeracion(): Observable<PerfilUsuarioResponse[]> {
    return this.http.get<PerfilUsuarioResponse[]>(`${this.gatewayUrl}/perfiles/moderacion/pendientes`);
  }

  moderarPerfil(
    usuarioId: number,
    request: ModeracionPerfilRequest,
  ): Observable<PerfilUsuarioResponse> {
    return this.http.patch<PerfilUsuarioResponse>(
      `${this.gatewayUrl}/perfiles/${usuarioId}/moderacion`,
      request,
    );
  }
}