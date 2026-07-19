import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { AuthService } from '../../auth/auth.service';
import { ProfileService } from '../../services/perfil/perfil-service';
import {
  AvatarCode,
  AVATAR_OPTIONS,
  DEFAULT_PROFILE_UPDATE,
  ModeracionPerfilRequest,
  PerfilUsuarioResponse,
  PerfilUsuarioUpdateRequest,
} from '../../models/perfil/perfil-model';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './perfil.html',
})
export class PerfilComponent implements OnInit {
  private authService = inject(AuthService);
  private profileService = inject(ProfileService);

  avatarOptions = AVATAR_OPTIONS;
  private avatarCodes = new Set<AvatarCode>(AVATAR_OPTIONS.map((option) => option.code));
  private currentUserId = this.authService.getUserId();

  profile = signal<PerfilUsuarioResponse | null>(null);
  profileForm = signal<PerfilUsuarioUpdateRequest>({ ...DEFAULT_PROFILE_UPDATE });

  isLoading = signal(true);
  isSaving = signal(false);
  loadError = signal<string | null>(null);
  saveError = signal<string | null>(null);
  saveFeedback = signal<string | null>(null);

  isAdmin = signal(this.authService.hasRole('ADMIN'));
  pendingProfiles = signal<PerfilUsuarioResponse[]>([]);
  isLoadingPending = signal(false);
  pendingError = signal<string | null>(null);
  pendingActionError = signal<string | null>(null);
  moderationComments = signal<Record<number, string>>({});
  moderationBusyIds = signal<Set<number>>(new Set());

  ngOnInit(): void {
    if (this.currentUserId === null) {
      this.loadError.set('No fue posible identificar tu usuario desde el token.');
      this.isLoading.set(false);

      if (this.isAdmin()) {
        this.cargarPendientesModeracion();
      }

      return;
    }

    this.cargarPerfil();

    if (this.isAdmin()) {
      this.cargarPendientesModeracion();
    }
  }

  cargarPerfil(): void {
    if (this.currentUserId === null) {
      return;
    }

    this.isLoading.set(true);
    this.loadError.set(null);

    this.profileService.obtenerPerfil(this.currentUserId).subscribe({
      next: (perfil) => {
        this.profile.set(perfil);
        this.inicializarFormulario(perfil);
        this.isLoading.set(false);
      },
      error: (error: unknown) => {
        this.loadError.set(this.extraerMensajeError(error, 'No se pudo cargar tu perfil.'));
        this.isLoading.set(false);
      },
    });
  }

  guardarPerfil(): void {
    if (this.currentUserId === null || this.isSaving()) {
      return;
    }

    const form = this.profileForm();
    const nombrePerfil = form.nombrePerfil.trim();

    if (!nombrePerfil) {
      this.saveError.set('El nombre de perfil es obligatorio.');
      this.saveFeedback.set(null);
      return;
    }

    const payload: PerfilUsuarioUpdateRequest = {
      ...form,
      nombrePerfil,
      descripcionPersonal: form.descripcionPersonal.trim(),
    };

    this.isSaving.set(true);
    this.saveError.set(null);
    this.saveFeedback.set(null);

    this.profileService
      .actualizarPerfil(this.currentUserId, payload)
      .pipe(finalize(() => this.isSaving.set(false)))
      .subscribe({
        next: (perfil) => {
          this.profile.set(perfil);
          this.inicializarFormulario(perfil);
          this.saveFeedback.set(`Cambios enviados. Estado de moderación: ${perfil.estadoModeracion}.`);
        },
        error: (error: unknown) => {
          this.saveError.set(this.extraerMensajeError(error, 'No se pudo guardar el perfil.'));
        },
      });
  }

  onAvatarSelect(avatar: AvatarCode): void {
    this.profileForm.update((form) => ({ ...form, fotoPerfil: avatar }));
  }

  onNombrePerfilChange(value: string): void {
    this.profileForm.update((form) => ({ ...form, nombrePerfil: value }));
  }

  onDescripcionChange(value: string): void {
    this.profileForm.update((form) => ({ ...form, descripcionPersonal: value }));
  }

  onEmailPrivadoChange(value: boolean): void {
    this.profileForm.update((form) => ({ ...form, emailPrivado: value }));
  }

  onNombrePrivadoChange(value: boolean): void {
    this.profileForm.update((form) => ({ ...form, nombrePrivado: value }));
  }

  getAvatarIcon(codigo: string | null | undefined): string {
    return this.avatarOptions.find((option) => option.code === codigo)?.icon ?? '🦊';
  }

  getEstadoBadgeClasses(estado: string): string {
    if (estado === 'APROBADO') {
      return 'bg-green-100 text-green-700';
    }

    if (estado === 'RECHAZADO') {
      return 'bg-red-100 text-red-700';
    }

    return 'bg-yellow-100 text-yellow-700';
  }

  cargarPendientesModeracion(): void {
    this.isLoadingPending.set(true);
    this.pendingError.set(null);

    this.profileService.listarPendientesModeracion().subscribe({
      next: (profiles) => {
        this.pendingProfiles.set(profiles);
        this.isLoadingPending.set(false);
      },
      error: (error: unknown) => {
        this.pendingError.set(this.extraerMensajeError(error, 'No se pudieron cargar los perfiles pendientes.'));
        this.isLoadingPending.set(false);
      },
    });
  }

  getModerationComment(usuarioId: number): string {
    return this.moderationComments()[usuarioId] ?? '';
  }

  setModerationComment(usuarioId: number, comentario: string): void {
    this.moderationComments.update((current) => ({
      ...current,
      [usuarioId]: comentario,
    }));
  }

  isModerating(usuarioId: number): boolean {
    return this.moderationBusyIds().has(usuarioId);
  }

  aprobarPerfil(usuarioId: number): void {
    this.moderarPerfil(usuarioId, true);
  }

  rechazarPerfil(usuarioId: number): void {
    this.moderarPerfil(usuarioId, false);
  }

  private moderarPerfil(usuarioId: number, aprobado: boolean): void {
    if (this.isModerating(usuarioId)) {
      return;
    }

    this.pendingActionError.set(null);

    const comentario = this.getModerationComment(usuarioId).trim();
    const request: ModeracionPerfilRequest = comentario ? { aprobado, comentario } : { aprobado };

    this.moderationBusyIds.update((current) => {
      const next = new Set(current);
      next.add(usuarioId);
      return next;
    });

    this.profileService
      .moderarPerfil(usuarioId, request)
      .pipe(
        finalize(() => {
          this.moderationBusyIds.update((current) => {
            const next = new Set(current);
            next.delete(usuarioId);
            return next;
          });
        }),
      )
      .subscribe({
        next: (perfilActualizado) => {
          this.pendingProfiles.update((profiles) =>
            profiles.filter((profile) => profile.usuarioId !== usuarioId),
          );

          this.moderationComments.update((comments) => {
            const next = { ...comments };
            delete next[usuarioId];
            return next;
          });

          if (this.profile()?.usuarioId === perfilActualizado.usuarioId) {
            this.profile.set(perfilActualizado);
            this.inicializarFormulario(perfilActualizado);
          }
        },
        error: (error: unknown) => {
          this.pendingActionError.set(
            this.extraerMensajeError(error, 'No se pudo aplicar la moderación.'),
          );
        },
      });
  }

  private inicializarFormulario(perfil: PerfilUsuarioResponse): void {
    this.profileForm.set({
      fotoPerfil: this.normalizeAvatarCode(perfil.fotoPerfil),
      nombrePerfil: perfil.nombrePerfil ?? '',
      descripcionPersonal: perfil.descripcionPersonal ?? '',
      emailPrivado: perfil.emailPrivado,
      nombrePrivado: perfil.nombrePrivado,
    });
  }

  private normalizeAvatarCode(codigo: string | null | undefined): AvatarCode {
    if (codigo && this.avatarCodes.has(codigo as AvatarCode)) {
      return codigo as AvatarCode;
    }

    return DEFAULT_PROFILE_UPDATE.fotoPerfil;
  }

  private extraerMensajeError(error: unknown, fallback: string): string {
    if (error instanceof HttpErrorResponse) {
      const rawError = error.error;

      if (typeof rawError === 'string' && rawError.trim()) {
        return rawError;
      }

      if (rawError && typeof rawError === 'object' && 'message' in rawError) {
        const message = (rawError as { message?: unknown }).message;
        if (typeof message === 'string' && message.trim()) {
          return message;
        }
      }
    }

    return fallback;
  }
}