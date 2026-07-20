import { Routes } from '@angular/router';
import { ShellComponent } from './layouts/shell';
import { authGuard } from './auth/auth.guard';
import { adminGuard } from './auth/admin.guard';
import { inject } from '@angular/core';
import { AuthService } from './auth/auth.service';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then(m => m.LoginComponent),
  },
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: 'callback', redirectTo: '', pathMatch: 'full' },
      
      { path: 'home', loadComponent: () => import('./pages/home/home').then(m => m.HomeComponent), canActivate: [authGuard] },
      { path: 'perfil', loadComponent: () => import('./pages/perfil/perfil').then(m => m.PerfilComponent), canActivate: [authGuard] },
      { path: 'historial', loadComponent: () => import('./pages/historial/historial').then(m => m.HistorialComponent), canActivate: [authGuard] },
      { path: 'amigos', loadComponent: () => import('./pages/amigos/amigos').then(m => m.AmigosComponent), canActivate: [authGuard] },
      { path: 'mis-comentarios', loadComponent: () => import('./pages/mis-comentarios/mis-comentarios').then(m => m.MisComentariosComponent), canActivate: [authGuard] },
      
      {
        path: 'admin/posts',
        loadComponent: () => import('./pages/admin/admin-posts/admin-posts').then(m => m.AdminPostsComponent),
        canActivate: [authGuard, adminGuard]
      },
      {
        path: 'admin/users',
        loadComponent: () => import('./pages/admin/admin-users/admin-users').then(m => m.AdminUsersComponent),
        canActivate: [authGuard, adminGuard]
      },
      {
        path: 'admin/restricciones',
        loadComponent: () => import('./pages/admin/admin-restricciones/admin-restricciones').then(m => m.AdminRestriccionesComponent),
        canActivate: [authGuard, adminGuard]
      },

      { 
        path: '', 
        pathMatch: 'full',
        redirectTo: () => {
          const authService = inject(AuthService);
          if (!authService.isLoggedIn) {
            return '/login';
          }
          const roles = authService.getRole();
          
          if (roles?.includes('ADMIN')) {
            return '/admin/posts';
          }
          
          return '/home';
        }
      },
    ],
  },
  { path: 'logout', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' },
];
