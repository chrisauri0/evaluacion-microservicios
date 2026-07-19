import { Routes } from '@angular/router';
import { ShellComponent } from './layouts/shell';
import { authGuard } from './auth/auth.guard';
import { adminGuard } from './auth/admin.guard';
import { inject } from '@angular/core';
import { AuthService } from './auth/auth.service';

export const routes: Routes = [
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: 'callback', redirectTo: '', pathMatch: 'full' },
      
      { path: 'home', loadComponent: () => import('./pages/home/home').then(m => m.HomeComponent), canActivate: [authGuard] },
      { path: 'perfil', loadComponent: () => import('./pages/perfil/perfil').then(m => m.PerfilComponent), canActivate: [authGuard] },
      { path: 'historial', loadComponent: () => import('./pages/historial/historial').then(m => m.HistorialComponent), canActivate: [authGuard] },
      { path: 'amigos', loadComponent: () => import('./pages/amigos/amigos').then(m => m.AmigosComponent), canActivate: [authGuard] },
      
      {
        path: 'admin/posts',
        loadComponent: () => import('./pages/admin/admin-posts/admin-posts').then(m => m.AdminPostsComponent),
        canActivate: [authGuard, adminGuard]
      },

      { 
        path: '', 
        pathMatch: 'full',
        redirectTo: () => {
          const authService = inject(AuthService);
          const roles = authService.getRole();
          
          if (roles && roles.includes('ADMIN')) {
            return '/admin/posts';
          }
          
          return '/home';
        }
      },
    ],
  },
];
