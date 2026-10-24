import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RestaurantDetailComponent } from './pages/restaurant-detail/restaurant-detail.component';
import { CheckoutComponent } from './pages/checkout/checkout.component';
import { TrackOrderComponent } from './pages/track-order/track-order.component';
import { OrdersComponent } from './pages/orders/orders.component';
import { OffersComponent } from './pages/offers/offers.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'restaurant/:id', component: RestaurantDetailComponent },
  { path: 'checkout', component: CheckoutComponent },
  { path: 'track', component: TrackOrderComponent },
  { path: 'track/:id', component: TrackOrderComponent },
  { path: 'orders', component: OrdersComponent },
  { path: 'offers', component: OffersComponent },
  { path: 'login', component: LoginComponent },
  { path: '**', redirectTo: '' }
];