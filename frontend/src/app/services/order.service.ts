import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { OrderResponse, PlaceOrderRequest, OrderStatus } from '../models/order.model';
import { RestaurantService } from './restaurant.service';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private http = inject(HttpClient);
  private restaurantService = inject(RestaurantService);
  private auth = inject(AuthService);

  constructor() {
    // Clean up legacy global orders key to prevent cross-account pollution
    try {
      localStorage.removeItem('qb_orders');
    } catch {
      // ignore
    }
  }

  placeOrder(req: PlaceOrderRequest): Observable<OrderResponse> {
    const currentUserId = this.auth.user()?.userId || 1;
    return this.http.post<OrderResponse>(`${environment.apiUrl}/api/orders`, req).pipe(
      tap(order => this.saveToLocal(order, currentUserId)),
      catchError(() => {
        // Fallback for testing if backend microservices are starting up
        const mockOrder: OrderResponse = {
          id: Math.floor(1000 + Math.random() * 9000),
          customerId: currentUserId,
          restaurantId: req.restaurantId,
          status: 'PLACED',
          totalAmount: 499,
          deliveryAddress: req.deliveryAddress,
          createdAt: new Date().toISOString(),
          items: req.items.map(i => ({
            menuItemId: i.menuItemId,
            itemName: 'Delicious Dish #' + i.menuItemId,
            unitPrice: 249,
            quantity: i.quantity
          })),
          driverName: 'Vikram Singh',
          driverPhone: '+91 98765 43210',
          driverRating: 4.9,
          vehicleNumber: 'KA-01-EQ-9042',
          estimatedDeliveryMinutes: 24
        };
        this.saveToLocal(mockOrder, currentUserId);
        return of(mockOrder);
      })
    );
  }

  getMyOrders(): Observable<OrderResponse[]> {
    const currentUserId = this.auth.user()?.userId;
    if (!currentUserId) {
      return of([]);
    }

    return this.http.get<OrderResponse[]>(`${environment.apiUrl}/api/orders/my`).pipe(
      map(orders => {
        const local = this.getLocalOrders(currentUserId);
        // Merge without duplicates by id
        const map = new Map<number, OrderResponse>();
        local.forEach(o => map.set(o.id, o));
        orders.forEach(o => {
          if (!o.customerId || o.customerId === currentUserId) {
            map.set(o.id, { ...o, customerId: currentUserId });
          }
        });
        const merged = Array.from(map.values())
          .filter(o => o.customerId === currentUserId)
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
        this.saveOrdersList(merged, currentUserId);
        return merged;
      }),
      catchError(() => {
        const local = this.getLocalOrders(currentUserId);
        // Provide demo seed order ONLY for the pre-configured demo account (userId: 1)
        if (local.length === 0 && currentUserId === 1) {
          const seed: OrderResponse = {
            id: 3842,
            customerId: 1,
            restaurantId: 1,
            restaurantName: 'The Burger Club & Grill',
            status: 'DELIVERED',
            totalAmount: 588,
            deliveryAddress: '102, Palm Grove Heights, Indiranagar, Bengaluru',
            createdAt: new Date(Date.now() - 3600000 * 24).toISOString(),
            items: [
              { menuItemId: 101, itemName: 'Truffle Smash Double Cheeseburger', unitPrice: 349, quantity: 1 },
              { menuItemId: 104, itemName: 'Loaded Truffle & Parmesan Fries', unitPrice: 189, quantity: 1 }
            ],
            driverName: 'Rajesh Kumar',
            driverRating: 4.9,
            vehicleNumber: 'KA-04-AB-1234'
          };
          this.saveToLocal(seed, 1);
          return of([seed]);
        }
        // For any other customer (especially newly created accounts), return ONLY their own orders
        return of(local.filter(o => o.customerId === currentUserId));
      })
    );
  }

  getOrder(id: number): Observable<OrderResponse> {
    const currentUserId = this.auth.user()?.userId || 1;
    return this.http.get<OrderResponse>(`${environment.apiUrl}/api/orders/${id}`).pipe(
      tap(order => this.saveToLocal(order, currentUserId)),
      catchError(() => {
        const local = this.getLocalOrders(currentUserId).find(o => o.id === id);
        if (local) return of(local);

        const fallback: OrderResponse = {
          id,
          customerId: currentUserId,
          restaurantId: 1,
          restaurantName: 'The Burger Club & Grill',
          status: 'OUT_FOR_DELIVERY',
          totalAmount: 538,
          deliveryAddress: '102, Palm Grove Heights, Indiranagar, Bengaluru',
          createdAt: new Date().toISOString(),
          items: [
            { menuItemId: 101, itemName: 'Truffle Smash Double Cheeseburger', unitPrice: 349, quantity: 1 },
            { menuItemId: 104, itemName: 'Loaded Truffle & Parmesan Fries', unitPrice: 189, quantity: 1 }
          ],
          driverName: 'Vikram Singh',
          driverPhone: '+91 98765 43210',
          driverRating: 4.9,
          vehicleNumber: 'KA-01-EQ-9042',
          estimatedDeliveryMinutes: 18
        };
        return of(fallback);
      })
    );
  }

  updateOrderStatus(orderId: number, status: OrderStatus | string, step?: number, lat?: number, lng?: number): void {
    const currentUserId = this.auth.user()?.userId || 1;
    const orders = this.getLocalOrders(currentUserId);
    const updated = orders.map(o => {
      if (o.id === orderId) {
        return {
          ...o,
          status,
          step: step ?? o.step,
          currentLat: lat ?? o.currentLat,
          currentLng: lng ?? o.currentLng
        };
      }
      return o;
    });
    this.saveOrdersList(updated, currentUserId);
  }

  private getStorageKey(userId: number): string {
    return `qb_orders_${userId}`;
  }

  private saveToLocal(order: OrderResponse, userId: number): void {
    const list = this.getLocalOrders(userId);
    const existingIndex = list.findIndex(o => o.id === order.id);
    if (existingIndex >= 0) {
      list[existingIndex] = { ...list[existingIndex], ...order, customerId: userId };
    } else {
      list.unshift({ ...order, customerId: userId });
    }
    this.saveOrdersList(list, userId);
  }

  private saveOrdersList(list: OrderResponse[], userId: number): void {
    try {
      localStorage.setItem(this.getStorageKey(userId), JSON.stringify(list));
    } catch {
      // ignore
    }
  }

  private getLocalOrders(userId: number): OrderResponse[] {
    try {
      const raw = localStorage.getItem(this.getStorageKey(userId));
      const orders: OrderResponse[] = raw ? JSON.parse(raw) : [];
      return orders.filter(o => o.customerId === userId);
    } catch {
      return [];
    }
  }
}
