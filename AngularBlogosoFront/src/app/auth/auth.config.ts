import { AuthConfig } from 'angular-oauth2-oidc';

export const authConfig: AuthConfig = {
  issuer: 'http://localhost:9000',
  redirectUri: window.location.origin + '/callback',
  postLogoutRedirectUri: window.location.origin + '/logout',
  clientId: 'angular-client',
  responseType: 'code',
  scope: 'openid profile posts.read posts.write usuarios.read usuarios.write',
  showDebugInformation: true,
  requireHttps: false,
  useSilentRefresh: false,
  strictDiscoveryDocumentValidation: false,
};
