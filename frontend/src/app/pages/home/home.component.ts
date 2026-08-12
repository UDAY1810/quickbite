import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  template: `
    <div class="wrap">
      <h1>QuickBite</h1>
      <p>Signed in as <b>{{ auth.user()?.fullName }}</b>
         ({{ auth.user()?.role }})</p>
      <button (click)="logout()">Log out</button>
    </div>
  `,
  styles: [`
    .wrap { padding: 48px; font-family: system-ui, sans-serif; }
    h1 { color: #D35400; }
    button { padding: 8px 16px; border: 1px solid #ddd; border-radius: 6px;
             background: #fff; cursor: pointer; }
  `]
})
export class HomeComponent {
  auth = inject(AuthService);
  private router = inject(Router);

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}