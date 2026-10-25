import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { adminGuard, vendorGuard, customerGuard } from './role.guard';
import { AuthService } from '../services/auth.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { firstValueFrom } from 'rxjs';

describe('Role Independence Guards', () => {
  let authService: AuthService;
  let router: Router;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: Router,
          useValue: {
            navigate: vi.fn()
          }
        }
      ]
    });

    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  async function loginMockUser(email: string, pass: string) {
    const p = firstValueFrom(authService.login({ email, password: pass }));
    const req = httpMock.expectOne(req => req.url.includes('/api/auth/login'));
    req.error(new ProgressEvent('error'), { status: 0, statusText: 'Offline' });
    return await p;
  }

  it('customerGuard should allow customers and guests, but redirect vendor to /vendor and admin to /admin', async () => {
    // 1. Guest
    authService.logout();
    const guestAllowed = TestBed.runInInjectionContext(() => customerGuard({} as any, {} as any));
    expect(guestAllowed).toBe(true);

    // 2. Customer
    await loginMockUser('uday@test.com', 'password123');
    const custAllowed = TestBed.runInInjectionContext(() => customerGuard({} as any, {} as any));
    expect(custAllowed).toBe(true);

    // 3. Vendor
    await loginMockUser('vendor@quickbite.com', 'vendor123');
    const vendorAllowed = TestBed.runInInjectionContext(() => customerGuard({} as any, {} as any));
    expect(vendorAllowed).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/vendor']);

    // 4. Admin
    await loginMockUser('admin@quickbite.com', 'admin123');
    const adminAllowed = TestBed.runInInjectionContext(() => customerGuard({} as any, {} as any));
    expect(adminAllowed).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/admin']);
  });

  it('vendorGuard should strictly allow vendors only, redirecting customers to /login and admin to /admin', async () => {
    // 1. Customer
    await loginMockUser('uday@test.com', 'password123');
    const custAccess = TestBed.runInInjectionContext(() => vendorGuard({} as any, {} as any));
    expect(custAccess).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);

    // 2. Admin
    await loginMockUser('admin@quickbite.com', 'admin123');
    const adminAccess = TestBed.runInInjectionContext(() => vendorGuard({} as any, {} as any));
    expect(adminAccess).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/admin']);

    // 3. Vendor
    await loginMockUser('vendor@quickbite.com', 'vendor123');
    const vendorAccess = TestBed.runInInjectionContext(() => vendorGuard({} as any, {} as any));
    expect(vendorAccess).toBe(true);
  });

  it('adminGuard should strictly allow admin only, redirecting vendors to /vendor and customers to /login', async () => {
    // 1. Customer
    await loginMockUser('uday@test.com', 'password123');
    const custAccess = TestBed.runInInjectionContext(() => adminGuard({} as any, {} as any));
    expect(custAccess).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);

    // 2. Vendor
    await loginMockUser('vendor@quickbite.com', 'vendor123');
    const vendorAccess = TestBed.runInInjectionContext(() => adminGuard({} as any, {} as any));
    expect(vendorAccess).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/vendor']);

    // 3. Admin
    await loginMockUser('admin@quickbite.com', 'admin123');
    const adminAccess = TestBed.runInInjectionContext(() => adminGuard({} as any, {} as any));
    expect(adminAccess).toBe(true);
  });
});
