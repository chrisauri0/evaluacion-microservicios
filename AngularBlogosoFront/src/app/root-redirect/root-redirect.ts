import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-root-redirect',
  standalone: true,
  template: '',
})
export class RootRedirectComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  ngOnInit(): void {
    if (!this.authService.isLoggedIn) {
      this.authService.login();
      return;
    }

    const roles = this.authService.getRole();
    const target = roles && roles.includes('ADMIN') ? '/admin/posts' : '/home';
    this.router.navigateByUrl(target);
  }
}
