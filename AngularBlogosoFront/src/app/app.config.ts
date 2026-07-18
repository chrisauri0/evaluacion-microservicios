import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection, APP_INITIALIZER } from '@angular/core';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth/auth.service';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './auth/auth.interceptor';
import { routes } from './app.routes';
import { provideOAuthClient } from 'angular-oauth2-oidc';

function initAuthFactory(authService: AuthService) {
  return () => authService.initAuth();
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideOAuthClient(),
    AuthService,
    {
      provide: APP_INITIALIZER, // <-- el token real, no el string
      useFactory: initAuthFactory,
      deps: [AuthService],
      multi: true,
    },
  ]
};