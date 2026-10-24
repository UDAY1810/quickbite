import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterOutlet, RouterLinkActive } from '@angular/router';
import { AuthService } from './services/auth.service';
import { CartService } from './services/cart.service';
import { CartDrawerComponent } from './components/cart-drawer/cart-drawer.component';
import { ToastComponent } from './components/toast/toast.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CartDrawerComponent, ToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  auth = inject(AuthService);
  cart = inject(CartService);
  private router = inject(Router);

  mobileOpen = false;
  locationDropdownOpen = false;
  currentLocation = 'Indiranagar, Bengaluru';

  locations = [
    'Indiranagar, Bengaluru',
    'Koramangala, Bengaluru',
    'Church Street, MG Road',
    'HSR Layout, Bengaluru',
    'Whitefield, Bengaluru'
  ];

  selectLocation(loc: string): void {
    this.currentLocation = loc;
    this.cart.setAddress('102, Palm Grove Heights, ' + loc);
    this.locationDropdownOpen = false;
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
