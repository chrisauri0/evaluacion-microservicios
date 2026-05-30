import { Component, inject, signal, effect } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProfileService } from '../../services/perfil/perfil-service';
import { AVATARS } from '../../models/perfil/perfil-model';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './perfil.html',
})
export class PerfilComponent {
  private profileService = inject(ProfileService);

  profile = this.profileService.profile;
  avatars = AVATARS;

  savedIndicator = signal(false);
  private saveTimeout?: ReturnType<typeof setTimeout>;

  constructor() {
    // Muestra "Cambios guardados" brevemente cada vez que el perfil cambia
    effect(() => {
      this.profile();
      this.savedIndicator.set(true);
      clearTimeout(this.saveTimeout);
      this.saveTimeout = setTimeout(() => this.savedIndicator.set(false), 1500);
    });
  }

  onNameChange(value: string): void {
    this.profileService.updateName(value);
  }

  onBioChange(value: string): void {
    this.profileService.updateBio(value);
  }

  onAvatarSelect(avatar: string): void {
    this.profileService.updateAvatar(avatar);
  }
}