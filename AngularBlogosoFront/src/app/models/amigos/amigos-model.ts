export interface UsuarioAmigo {
  usuarioId: number;
  username: string;
  nombrePerfil: string | null;
  fotoPerfil: string;
}

export interface SolicitudAmistadDto {
  id?: number;
  emisorId: number;
  receptorId: number;
  estado?: string; // PENDIENTE, ACEPTADA, RECHAZADA
  emisorNombre?: string;
}

export interface SolicitudAmistadRequest {
  remitenteId: number;
  destinatarioId: number;
}
