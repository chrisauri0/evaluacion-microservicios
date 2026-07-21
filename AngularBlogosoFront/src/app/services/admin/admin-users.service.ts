import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UsuarioDto {
  id: number;
  username: string;
  email: string;
  nombre: string;
  fechaRegistro?: string;
  rol?: string;
  avatarUrl?: string;
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
  private http = inject(HttpClient);
  private gatewayUrl = 'http://localhost:8080/api/usuarios';

  getAllUsers(): Observable<UsuarioDto[]> {
    return this.http.get<UsuarioDto[]>(this.gatewayUrl);
  }

  getUserById(id: number): Observable<UsuarioDto> {
    return this.http.get<UsuarioDto>(`${this.gatewayUrl}/${id}`);
  }

  createUser(request: RegistroRequest): Observable<UsuarioDto> {
    return this.http.post<UsuarioDto>(this.gatewayUrl, request);
  }

  updateUser(id: number, request: Partial<RegistroRequest>): Observable<UsuarioDto> {
    return this.http.put<UsuarioDto>(`${this.gatewayUrl}/${id}`, request);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.gatewayUrl}/${id}`);
  }
}
