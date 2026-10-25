import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, of, catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';

export interface StoredUser {
  id: number;
  email: string;
  password: string;
  fullName: string;
  phone?: string;
  role: string;
  createdAt?: string;
}

const DEFAULT_USERS: StoredUser[] = [
  {
    id: 1,
    email: 'uday@test.com',
    password: 'password123',
    fullName: 'Uday Dubey',
    phone: '+91 98765 43210',
    role: 'CUSTOMER',
    createdAt: '2026-03-15T10:00:00.000Z'
  },
  {
    id: 2,
    email: 'vendor@quickbite.com',
    password: 'vendor123',
    fullName: 'Chef Marco Rossi (Vendor)',
    phone: '+91 98111 22334',
    role: 'RESTAURANT_OWNER',
    createdAt: '2026-02-10T09:30:00.000Z'
  },
  {
    id: 3,
    email: 'admin@quickbite.com',
    password: 'admin123',
    fullName: 'QuickBite Administrator',
    phone: '+91 99999 88888',
    role: 'ADMIN',
    createdAt: '2026-01-01T08:00:00.000Z'
  },
  {
    id: 4,
    email: 'priya.sharma@example.com',
    password: 'password123',
    fullName: 'Priya Sharma',
    phone: '+91 91234 56789',
    role: 'CUSTOMER',
    createdAt: '2026-04-12T14:20:00.000Z'
  },
  {
    id: 5,
    email: 'rohit.cafe@example.com',
    password: 'password123',
    fullName: 'Rohit Gupta (Artisan Roastery)',
    phone: '+91 97777 66666',
    role: 'RESTAURANT_OWNER',
    createdAt: '2026-03-25T11:15:00.000Z'
  }
];

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
          // If backend responded with explicit authentication or client failure, reject
          if (err.status === 401 || err.status === 400) {
            return throwError(() => err);
          }

          // If backend is offline or unreachable (status 0, 404, 503)
          if (err.status === 0 || err.status === 404 || err.status === 503) {
            const users = this.getStoredUsers();
            const normalizedEmail = (req.email || '').trim().toLowerCase();
            const matchedUser = users.find(u => u.email.toLowerCase() === normalizedEmail);

            // Strictly reject wrong email or wrong password!
            if (!matchedUser || matchedUser.password !== req.password) {
              return throwError(() => ({
                status: 401,
                error: { message: 'Wrong email or password' }
              }));
            }

            // Valid credentials matched registered user or demo user
            const authRes: AuthResponse = {
              token: `offline-token-${matchedUser.id}-${Date.now()}`,
              userId: matchedUser.id,
              fullName: matchedUser.fullName,
              role: matchedUser.role,
              email: matchedUser.email
            };
            this.store(authRes);
            return of(authRes);
          }

          return throwError(() => err);
        })
      );
  }

  register(req: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/api/auth/register`, req)
      .pipe(
        tap(res => {
          this.addStoredUser({
            id: res.userId,
            email: req.email.trim().toLowerCase(),
            password: req.password,
            fullName: req.fullName,
            phone: req.phone,
            role: req.role || res.role || 'CUSTOMER',
            createdAt: new Date().toISOString()
          });
          this.store(res);
        }),
        catchError(err => {
          // If backend explicitly rejected (e.g. 409 conflict, 400 bad request)
          if (err.status === 409 || err.status === 400) {
            return throwError(() => err);
          }

          // If backend is offline or unreachable (status 0, 404, 503)
          if (err.status === 0 || err.status === 404 || err.status === 503) {
            const users = this.getStoredUsers();
            const normalizedEmail = (req.email || '').trim().toLowerCase();
            const existing = users.find(u => u.email.toLowerCase() === normalizedEmail);

            if (existing) {
              return throwError(() => ({
                status: 409,
                error: { message: 'An account with this email already exists' }
              }));
            }

            // Generate unique user ID for the newly created user
            const newUserId = Date.now();
            const assignedRole = req.role || 'CUSTOMER';
            const newUser: StoredUser = {
              id: newUserId,
              email: normalizedEmail,
              password: req.password,
              fullName: req.fullName,
              phone: req.phone,
              role: assignedRole,
              createdAt: new Date().toISOString()
            };
            this.addStoredUser(newUser);

            const authRes: AuthResponse = {
              token: `offline-token-${newUserId}-${Date.now()}`,
              userId: newUserId,
              fullName: req.fullName,
              role: assignedRole,
              email: normalizedEmail
            };
            this.store(authRes);
            return of(authRes);
          }

          return throwError(() => err);
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
    localStorage.removeItem('qb_cart');
    localStorage.removeItem('qb_cart_rest_id');
    localStorage.removeItem('qb_cart_rest_name');
    localStorage.removeItem('qb_promo');
    this.token.set(null);
    this.user.set(null);
  }

  isLoggedIn(): boolean {
    return this.token() !== null;
  }

  isAdmin(): boolean {
    return this.user()?.role === 'ADMIN';
  }

  isVendor(): boolean {
    const role = this.user()?.role;
    return role === 'RESTAURANT_OWNER' || role === 'VENDOR';
  }

  getStoredUsers(): StoredUser[] {
    try {
      const raw = localStorage.getItem('qb_users_db');
      if (!raw) {
        localStorage.setItem('qb_users_db', JSON.stringify(DEFAULT_USERS));
        return [...DEFAULT_USERS];
      }
      const parsed: StoredUser[] = JSON.parse(raw);
      // Ensure default essential accounts always exist (Customer, Vendor, Admin)
      let modified = false;
      DEFAULT_USERS.forEach(def => {
        if (!parsed.some(p => p.email.toLowerCase() === def.email.toLowerCase())) {
          parsed.push(def);
          modified = true;
        }
      });
      if (modified) {
        localStorage.setItem('qb_users_db', JSON.stringify(parsed));
      }
      return parsed;
    } catch {
      return [...DEFAULT_USERS];
    }
  }

  getVendors(): StoredUser[] {
    return this.getStoredUsers().filter(u => u.role === 'RESTAURANT_OWNER' || u.role === 'VENDOR');
  }

  getCustomers(): StoredUser[] {
    return this.getStoredUsers().filter(u => u.role === 'CUSTOMER');
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

  private addStoredUser(user: StoredUser): void {
    const users = this.getStoredUsers();
    const idx = users.findIndex(u => u.email.toLowerCase() === user.email.toLowerCase());
    if (idx >= 0) {
      users[idx] = user;
    } else {
      users.push(user);
    }
    localStorage.setItem('qb_users_db', JSON.stringify(users));
  }
}