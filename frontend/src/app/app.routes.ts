import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RestaurantDetailComponent } from './pages/restaurant-detail/restaurant-detail.component';
import { CheckoutComponent } from './pages/checkout/checkout.component';
import { TrackOrderComponent } from './pages/track-order/track-order.component';
import { OrdersComponent } from './pages/orders/orders.component';
import { OffersComponent } from './pages/offers/offers.component';
import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard.component';
import { VendorPortalComponent } from './pages/vendor-portal/vendor-portal.component';
import { adminGuard, vendorGuard, customerGuard } from './guards/role.guard';

export const routes: Routes = [
  // Customer Routes (guarded against vendors/admins so they stay in their dedicated portals)
  { path: '', component: HomeComponent, canActivate: [customerGuard] },
  { path: 'restaurant/:id', component: RestaurantDetailComponent, canActivate: [customerGuard] },
  { path: 'checkout', component: CheckoutComponent, canActivate: [customerGuard] },
  { path: 'track', component: TrackOrderComponent, canActivate: [customerGuard] },
  { path: 'track/:id', component: TrackOrderComponent, canActivate: [customerGuard] },
  { path: 'orders', component: OrdersComponent, canActivate: [customerGuard] },
  { path: 'offers', component: OffersComponent, canActivate: [customerGuard] },

  // Admin Portal (strictly for ADMIN)
  { path: 'admin', component: AdminDashboardComponent, canActivate: [adminGuard] },

  // Vendor Portal (strictly for RESTAURANT_OWNER / VENDOR)
  { path: 'vendor', component: VendorPortalComponent, canActivate: [vendorGuard] },

  // Auth
  { path: 'login', component: LoginComponent },
  { path: '**', redirectTo: '' }
];