import { Injectable, ApplicationRef } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { authConfig } from './auth.config';

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

  get accessTokenClaims(): any {
    const token = this.oauthService.getAccessToken();
    if (!token) return null;
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
  }
  getRole(): any {
    const claims = this.accessTokenClaims;
    if (!claims) return null;
    return claims.roles || null; 
  }
}
