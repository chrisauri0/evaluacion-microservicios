import { Routes } from '@angular/router';
import { ShellComponent } from './layouts/shell';

export const routes: Routes = [
  {
    path: '',
    component: ShellComponent,
    children: [

      { path: 'callback', redirectTo: '', pathMatch: 'full' },
      { path: 'home', loadComponent: () => import('./pages/home/home').then(m => m.HomeComponent) },
      { path: 'perfil', loadComponent: () => import('./pages/perfil/perfil').then(m => m.PerfilComponent) },
      //{ path: 'roles', loadComponent: () => import('./pages/roles/roles.component').then(m => m.RolesComponent) },
      { path: 'historial', loadComponent: () => import('./pages/historial/historial').then(m => m.HistorialComponent) },
      { path: 'amigos', loadComponent: () => import('./pages/amigos/amigos').then(m => m.AmigosComponent) },
      { path: '', redirectTo: 'home', pathMatch: 'full' },
    ],
  },
];