import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SolicitudAmistadDto } from '../../models/amigos/amigos-model';

@Injectable({ providedIn: 'root' })
export class AmigosService {
  private http = inject(HttpClient);
  
  private gatewaySolicitudesUrl = 'http://localhost:8080/api/usuarios/solicitudes';
  private gatewayUsuariosUrl = 'http://localhost:8080/api/usuarios';

  enviar(dto: SolicitudAmistadDto): Observable<SolicitudAmistadDto> {
    return this.http.post<SolicitudAmistadDto>(this.gatewaySolicitudesUrl, dto);
  }

  listarPendientes(receptorId: number): Observable<SolicitudAmistadDto[]> {
    return this.http.get<SolicitudAmistadDto[]>(`${this.gatewaySolicitudesUrl}/pendientes/${receptorId}`);
  }

  actualizarEstado(id: number, estado: string): Observable<SolicitudAmistadDto> {
    const params = new HttpParams().set('estado', estado);
    return this.http.put<SolicitudAmistadDto>(`${this.gatewaySolicitudesUrl}/${id}/estado`, null, { params });
  }

  obtenerUsuarioPorId(id: number): Observable<any> {
    return this.http.get<any>(`${this.gatewayUsuariosUrl}/${id}`);
  }

  obtenerIdsDeAmigos(userId: number): Observable<number[]> {
    return this.http.get<number[]>(`${this.gatewaySolicitudesUrl}/amigos/${userId}`);
  }

  buscarPorUsername(username: string): Observable<any> {
    return this.http.get<any>(`${this.gatewayUsuariosUrl}/username/${username}`);
  }
}
