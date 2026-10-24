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

  fillDemo(): void {
    this.email = 'uday@test.com';
    this.password = 'password123';
    this.fullName = 'Uday Dubey';
    this.phone = '+91 98765 43210';
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
        phone: this.phone
      }).subscribe({
        next: () => {
          this.loading.set(false);
          this.toast.show(`Account created! Welcome, ${this.fullName}! 👋`, 'success');
          this.router.navigate(['/']);
        },
        error: (err) => {
          this.loading.set(false);
          this.error.set(
            err.status === 409
              ? 'An account with this email already exists'
              : 'Failed to create account. Please check your details.'
          );
        }
      });
    } else {
      this.auth.login({ email: this.email, password: this.password }).subscribe({
        next: () => {
          this.loading.set(false);
          this.toast.show('Signed in successfully! Ready to eat? 🍕', 'success');
          this.router.navigate(['/']);
        },
        error: (err) => {
          this.loading.set(false);
          this.error.set(
            err.status === 401
              ? 'Wrong email or password'
              : 'Sign in failed. Backend might be offline.'
          );
        }
      });
    }
  }
}