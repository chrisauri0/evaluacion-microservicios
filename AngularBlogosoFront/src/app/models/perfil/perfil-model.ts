export type EstadoModeracionPerfil = 'APROBADO' | 'PENDIENTE' | 'RECHAZADO';

export type AvatarCode =
  | 'AVATAR_1'
  | 'AVATAR_2'
  | 'AVATAR_3'
  | 'AVATAR_4'
  | 'AVATAR_5'
  | 'AVATAR_6'
  | 'AVATAR_7'
  | 'AVATAR_8';

export interface PerfilUsuarioResponse {
  usuarioId: number;
  username: string;
  nombrePerfil: string | null;
  email: string | null;
  descripcionPersonal: string | null;
  fotoPerfil: string;
  emailPrivado: boolean;
  nombrePrivado: boolean;
  estadoModeracion: EstadoModeracionPerfil;
  observacionModeracion: string | null;
  ultimaActualizacion: string | null;
}

export interface PerfilUsuarioUpdateRequest {
  fotoPerfil: AvatarCode;
  nombrePerfil: string;
  descripcionPersonal: string;
  emailPrivado: boolean;
  nombrePrivado: boolean;
}

export interface ModeracionPerfilRequest {
  aprobado: boolean;
  comentario?: string;
}

export interface AvatarOption {
  code: AvatarCode;
  icon: string;
  label: string;
}

export const AVATAR_OPTIONS: AvatarOption[] = [
  { code: 'AVATAR_1', icon: '🦊', label: 'Zorro' },
  { code: 'AVATAR_2', icon: '🐱', label: 'Gato' },
  { code: 'AVATAR_3', icon: '🐼', label: 'Panda' },
  { code: 'AVATAR_4', icon: '🐸', label: 'Rana' },
  { code: 'AVATAR_5', icon: '🦁', label: 'León' },
  { code: 'AVATAR_6', icon: '🐧', label: 'Pingüino' },
  { code: 'AVATAR_7', icon: '🐨', label: 'Koala' },
  { code: 'AVATAR_8', icon: '🐹', label: 'Hámster' },
];

export const DEFAULT_PROFILE_UPDATE: PerfilUsuarioUpdateRequest = {
  fotoPerfil: 'AVATAR_1',
  nombrePerfil: '',
  descripcionPersonal: '',
  emailPrivado: false,
  nombrePrivado: false,
};