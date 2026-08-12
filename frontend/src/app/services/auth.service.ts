import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse, LoginRequest } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private http = inject(HttpClient);

  // signals: Angular's reactive state. When these change, the UI updates.
  token = signal<string | null>(localStorage.getItem('qb_token'));
  user = signal<AuthResponse | null>(this.loadUser());

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/api/auth/login`, req)
      .pipe(tap(res => this.store(res)));
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