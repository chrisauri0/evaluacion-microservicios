import { Injectable, ApplicationRef } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { authConfig } from './auth.config';

type AccessTokenClaims = Record<string, unknown> & {
  roles?: string[] | string;
  userId?: string | number;
  sub?: string;
};

@Injectable({ providedIn: 'root' })
export class AuthService {

  constructor(
    private oauthService: OAuthService,
    private appRef: ApplicationRef
  ) {
    this.oauthService.configure(authConfig);
  }

  async initAuth(): Promise<void> {
    this.oauthService.setupAutomaticSilentRefresh();
    await this.oauthService.loadDiscoveryDocumentAndTryLogin();
    this.appRef.tick();
  }

  login(): void {
    this.oauthService.initCodeFlow();
  }

  logout(): void {
    this.oauthService.logOut();
  }

  get accessToken(): string {
    return this.oauthService.getAccessToken();
  }

  get isLoggedIn(): boolean {
    return this.oauthService.hasValidAccessToken();
  }

  get identityClaims(): any {
    return this.oauthService.getIdentityClaims();
  }

  get accessTokenClaims(): AccessTokenClaims | null {
    const token = this.oauthService.getAccessToken();
    if (!token) return null;

    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/'))) as AccessTokenClaims;
    } catch {
      return null;
    }
  }

  getRole(): any {
    const claims = this.accessTokenClaims;
    if (!claims) return null;
    const roles = claims.roles;

    if (Array.isArray(roles)) return roles;
    if (typeof roles === 'string') return [roles];
    return null;
  }

  hasRole(role: string): boolean {
    const roles = this.getRole();
    return Array.isArray(roles) && roles.some((r) => r.toUpperCase() === role.toUpperCase());
  }

  getUserId(): number | null {
    const claims = this.accessTokenClaims;
    if (!claims) return null;

    const fromUserId = this.parseNumericClaim(claims.userId);
    if (fromUserId !== null) {
      return fromUserId;
    }

    return this.parseNumericClaim(claims.sub);
  }

  private parseNumericClaim(value: unknown): number | null {
    if (typeof value === 'number' && Number.isFinite(value)) {
      return value;
    }

    if (typeof value === 'string' && /^\d+$/.test(value)) {
      return Number(value);
    }

    return null;
  }
}
