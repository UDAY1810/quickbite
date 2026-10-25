import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAdmin()) {
    return true;
  }

  if (auth.isVendor()) {
    router.navigate(['/vendor']);
    return false;
  }

  router.navigate(['/login']);
  return false;
};

export const vendorGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isVendor()) {
    return true;
  }

  if (auth.isAdmin()) {
    router.navigate(['/admin']);
    return false;
  }

  router.navigate(['/login']);
  return false;
};

export const customerGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  // If logged in as Vendor, redirect to Vendor Hub
  if (auth.isVendor()) {
    router.navigate(['/vendor']);
    return false;
  }

  // If logged in as Admin, redirect to Admin Portal
  if (auth.isAdmin()) {
    router.navigate(['/admin']);
    return false;
  }

  // Regular customers or guests are welcome on customer pages
  return true;
};
