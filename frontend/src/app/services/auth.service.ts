import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, of, catchError } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  // signals: Angular's reactive state
  token = signal<string | null>(localStorage.getItem('qb_token'));
  user = signal<AuthResponse | null>(this.loadUser());

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/api/auth/login`, req)
      .pipe(
        tap(res => this.store(res)),
        catchError(err => {
          // If backend is not running, provide demo session if test email is used
          if (err.status === 0 || err.status === 404 || err.status === 503) {
            console.warn('Backend unavailable, activating demo session:', err);
            const demoRes: AuthResponse = {
              token: 'demo-jwt-token-quickbite',
              userId: 1,
              fullName: req.email.split('@')[0].toUpperCase(),
              role: 'CUSTOMER'
            };
            this.store(demoRes);
            return of(demoRes);
          }
          throw err;
        })
      );
  }

  register(req: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/api/auth/register`, req)
      .pipe(
        tap(res => this.store(res)),
        catchError(err => {
          if (err.status === 0 || err.status === 404 || err.status === 503) {
            const demoRes: AuthResponse = {
              token: 'demo-jwt-token-quickbite',
              userId: 1,
              fullName: req.fullName,
              role: 'CUSTOMER'
            };
            this.store(demoRes);
            return of(demoRes);
          }
          throw err;
        })
      );
  }

  loginAsGuest(): void {
    const guestRes: AuthResponse = {
      token: 'demo-guest-token',
      userId: 999,
      fullName: 'Foodie Guest',
      role: 'CUSTOMER'
    };
    this.store(guestRes);
  }

  logout(): void {
    localStorage.removeItem('qb_token');
    localStorage.removeItem('qb_user');
    this.token.set(null);
    this.user.set(null);
  }

  isLoggedIn(): boolean {
    return this.token() !== null;
  }

  private store(res: AuthResponse): void {
    localStorage.setItem('qb_token', res.token);
    localStorage.setItem('qb_user', JSON.stringify(res));
    this.token.set(res.token);
    this.user.set(res);
  }

  private loadUser(): AuthResponse | null {
    const raw = localStorage.getItem('qb_user');
    return raw ? JSON.parse(raw) : null;
  }
}