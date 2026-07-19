import { Component, input, inject } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { RouterLink, RouterLinkActive } from '@angular/router';

interface NavItem {
  label: string;
  path: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
})
export class SidebarComponent {
  authService = inject(AuthService);

  pendingCount = input<number>(0);

  cerrarSesion() {
    this.authService.logout();
  }

  get isAdmin(): boolean {
    const roles = this.authService.getRole();
    return roles ? roles.includes('ADMIN') : false;
  }

  navItems: NavItem[] = [
    { label: 'Home', path: '/home' },
    { label: 'Perfil', path: '/perfil' },
    { label: 'Mi historial', path: '/historial' },
    { label: 'Amigos', path: '/amigos' },
  ];

  adminNavItems: NavItem[] = [
    { label: 'Gestión de Publicaciones', path: '/admin/posts' },
    { label: 'Gestión de Usuarios', path: '/admin/users' },
  ];
}
