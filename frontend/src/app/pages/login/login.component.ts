import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  private toast = inject(ToastService);

  isRegisterMode = false;

  email = '';
  password = '';
  fullName = '';
  phone = '';
  selectedRole: 'CUSTOMER' | 'RESTAURANT_OWNER' = 'CUSTOMER';

  error = signal<string | null>(null);
  loading = signal(false);

  setMode(register: boolean): void {
    this.isRegisterMode = register;
    this.error.set(null);
  }

  toggleMode(): void {
    this.isRegisterMode = !this.isRegisterMode;
    this.error.set(null);
  }

  fillCustomerDemo(): void {
    this.email = 'uday@test.com';
    this.password = 'password123';
    this.fullName = 'Uday Dubey';
    this.phone = '+91 98765 43210';
    this.selectedRole = 'CUSTOMER';
  }

  fillVendorDemo(): void {
    this.email = 'vendor@quickbite.com';
    this.password = 'vendor123';
    this.fullName = 'Chef Marco Rossi (Vendor)';
    this.phone = '+91 98111 22334';
    this.selectedRole = 'RESTAURANT_OWNER';
  }

  fillAdminDemo(): void {
    this.email = 'admin@quickbite.com';
    this.password = 'admin123';
    this.fullName = 'QuickBite Administrator';
    this.phone = '+91 99999 88888';
  }

  continueAsGuest(): void {
    this.auth.loginAsGuest();
    this.toast.show('Welcome to QuickBite! Exploring as guest foodie 🎉', 'success');
    this.router.navigate(['/']);
  }

  submit(): void {
    this.error.set(null);
    this.loading.set(true);

    if (this.isRegisterMode) {
      this.auth.register({
        email: this.email,
        password: this.password,
        fullName: this.fullName,
        phone: this.phone,
        role: this.selectedRole
      }).subscribe({
        next: (res) => {
          this.loading.set(false);
          this.toast.show(`Account created! Welcome, ${this.fullName}! 👋`, 'success');
          if (res.role === 'RESTAURANT_OWNER' || res.role === 'VENDOR') {
            this.router.navigate(['/vendor']);
          } else {
            this.router.navigate(['/']);
          }
        },
        error: (err) => {
          this.loading.set(false);
          this.error.set(
            err.status === 409
              ? 'An account with this email already exists'
              : (err.error?.message || 'Failed to create account. Please check your details.')
          );
        }
      });
    } else {
      this.auth.login({ email: this.email, password: this.password }).subscribe({
        next: (res) => {
          this.loading.set(false);
          this.toast.show(`Welcome back, ${res.fullName}! 🎉`, 'success');
          if (res.role === 'ADMIN') {
            this.router.navigate(['/admin']);
          } else if (res.role === 'RESTAURANT_OWNER' || res.role === 'VENDOR') {
            this.router.navigate(['/vendor']);
          } else {
            this.router.navigate(['/']);
          }
        },
        error: (err) => {
          this.loading.set(false);
          this.error.set(
            err.status === 401
              ? 'Wrong email or password'
              : (err.error?.message || 'Sign in failed. Please check your credentials.')
          );
        }
      });
    }
  }
}