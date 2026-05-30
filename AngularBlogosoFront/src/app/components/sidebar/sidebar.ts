import { Component, input } from '@angular/core';
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
  pendingCount = input<number>(0);

  navItems: NavItem[] = [
    { label: 'Home', path: '/home' },
    { label: 'Perfil', path: '/perfil' },
    { label: 'Mi historial', path: '/historial' },
    { label: 'Amigos', path: '/amigos' },
  ];
}