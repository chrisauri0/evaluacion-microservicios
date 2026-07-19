export type RolUsuario = 'ADMIN' | 'USER';

export interface Usuario {
  id: number;
  username: string;
  email: string;
  rol: RolUsuario;
  avatarUrl?: string;
}

export interface UsuarioRequest {
  username: string;
  email: string;
  rol: RolUsuario;
   password?: string; 
}
