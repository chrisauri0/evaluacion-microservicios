import { Injectable, signal } from '@angular/core';
import { Profile } from '../../models/perfil/perfil-model';

const DEFAULT_PROFILE: Profile = {
  name: 'Tú',
  bio: 'Explorando por aquí 👋',
  avatar: '🦊',
};

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private readonly _profile = signal<Profile>(DEFAULT_PROFILE);
  profile = this._profile.asReadonly();

  updateName(name: string): void {
    this._profile.update((p) => ({ ...p, name }));
  }

  updateBio(bio: string): void {
    this._profile.update((p) => ({ ...p, bio }));
  }

  updateAvatar(avatar: string): void {
    this._profile.update((p) => ({ ...p, avatar }));
  }
}