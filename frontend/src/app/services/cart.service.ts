import { Injectable, computed, signal, inject } from '@angular/core';
import { CartItem, PromoCode } from '../models/cart.model';
import { MenuItem, Restaurant } from '../models/restaurant.model';
import { ToastService } from './toast.service';

export const AVAILABLE_PROMOS: PromoCode[] = [
  { code: 'QUICK50', discountPercent: 50, maxDiscount: 150, minOrder: 199, description: '50% OFF up to ₹150 on your order' },
  { code: 'BITE100', flatDiscount: 100, minOrder: 399, description: 'Flat ₹100 OFF on orders above ₹399' },
  { code: 'FEAST20', discountPercent: 20, maxDiscount: 200, minOrder: 499, description: '20% OFF on party feasts above ₹499' },
  { code: 'FREESHIP', flatDiscount: 40, minOrder: 149, description: 'Free Delivery on any order above ₹149' }
];

@Injectable({ providedIn: 'root' })
export class CartService {
  private toast = inject(ToastService);

  restaurantId = signal<number | null>(this.loadStoredRestaurantId());
  restaurantName = signal<string | null>(this.loadStoredRestaurantName());
  items = signal<CartItem[]>(this.loadStoredItems());
  deliveryAddress = signal<string>(localStorage.getItem('qb_address') || '102, Palm Grove Heights, Indiranagar, Bengaluru');
  selectedPromo = signal<PromoCode | null>(this.loadStoredPromo());
  tipAmount = signal<number>(20);
  notes = signal<string>('');
  isDrawerOpen = signal<boolean>(false);

  // Computed values
  totalCount = computed(() => this.items().reduce((acc, curr) => acc + curr.quantity, 0));

  itemsSubtotal = computed(() => {
    return this.items().reduce((acc, curr) => acc + (curr.menuItem.price * curr.quantity), 0);
  });

  deliveryFee = computed(() => {
    const sub = this.itemsSubtotal();
    if (sub === 0) return 0;
    if (this.selectedPromo()?.code === 'FREESHIP') return 0;
    return sub >= 299 ? 0 : 35;
  });

  platformFee = computed(() => {
    return this.itemsSubtotal() > 0 ? 5 : 0;
  });

  taxes = computed(() => {
    const sub = this.itemsSubtotal();
    return Math.round(sub * 0.05); // 5% GST on restaurant food
  });

  discountAmount = computed(() => {
    const promo = this.selectedPromo();
    const sub = this.itemsSubtotal();
    if (!promo || sub < promo.minOrder) return 0;

    if (promo.flatDiscount) {
      return Math.min(promo.flatDiscount, sub);
    }
    if (promo.discountPercent) {
      const calculated = Math.round((sub * promo.discountPercent) / 100);
      return promo.maxDiscount ? Math.min(calculated, promo.maxDiscount) : calculated;
    }
    return 0;
  });

  grandTotal = computed(() => {
    const sub = this.itemsSubtotal();
    if (sub === 0) return 0;
    const total = sub + this.deliveryFee() + this.platformFee() + this.taxes() + this.tipAmount() - this.discountAmount();
    return Math.max(0, total);
  });

  openDrawer(): void {
    this.isDrawerOpen.set(true);
  }

  closeDrawer(): void {
    this.isDrawerOpen.set(false);
  }

  toggleDrawer(): void {
    this.isDrawerOpen.update(v => !v);
  }

  addItem(item: MenuItem, restaurant: { id: number; name: string }): void {
    const currentRestId = this.restaurantId();

    // Check if adding from another restaurant
    if (currentRestId && currentRestId !== restaurant.id && this.items().length > 0) {
      const confirmReset = confirm(
        `Your cart contains items from "${this.restaurantName()}". Would you like to clear your cart and add items from "${restaurant.name}"?`
      );
      if (!confirmReset) return;
      this.clearCart();
    }

    this.restaurantId.set(restaurant.id);
    this.restaurantName.set(restaurant.name);

    this.items.update(list => {
      const existing = list.find(i => i.menuItem.id === item.id);
      if (existing) {
        return list.map(i => i.menuItem.id === item.id ? { ...i, quantity: i.quantity + 1 } : i);
      } else {
        return [...list, { menuItem: item, quantity: 1 }];
      }
    });

    this.save();
    this.toast.show(`Added "${item.name}" to cart! 🛒`, 'success');
  }

  removeItem(itemId: number): void {
    this.items.update(list => list.filter(i => i.menuItem.id !== itemId));
    if (this.items().length === 0) {
      this.clearCart();
    } else {
      this.save();
    }
  }

  updateQuantity(itemId: number, delta: number): void {
    this.items.update(list => {
      return list
        .map(i => {
          if (i.menuItem.id === itemId) {
            const newQty = i.quantity + delta;
            return newQty > 0 ? { ...i, quantity: newQty } : null;
          }
          return i;
        })
        .filter((i): i is CartItem => i !== null);
    });

    if (this.items().length === 0) {
      this.clearCart();
    } else {
      this.save();
    }
  }

  getItemQuantity(itemId: number): number {
    const found = this.items().find(i => i.menuItem.id === itemId);
    return found ? found.quantity : 0;
  }

  setAddress(addr: string): void {
    this.deliveryAddress.set(addr);
    localStorage.setItem('qb_address', addr);
  }

  setTip(amount: number): void {
    this.tipAmount.set(amount);
  }

  applyPromo(code: string): { success: boolean; message: string } {
    const clean = code.trim().toUpperCase();
    const found = AVAILABLE_PROMOS.find(p => p.code === clean);

    if (!found) {
      return { success: false, message: 'Invalid promo code' };
    }

    if (this.itemsSubtotal() < found.minOrder) {
      return {
        success: false,
        message: `Min order value for ${found.code} is ₹${found.minOrder}. Add items worth ₹${found.minOrder - this.itemsSubtotal()} more!`
      };
    }

    this.selectedPromo.set(found);
    localStorage.setItem('qb_promo', JSON.stringify(found));
    this.toast.show(`Coupon ${found.code} applied! Saved ₹${this.discountAmount()}`, 'success');
    return { success: true, message: `Coupon ${found.code} applied successfully!` };
  }

  removePromo(): void {
    this.selectedPromo.set(null);
    localStorage.removeItem('qb_promo');
    this.toast.show('Coupon removed', 'info');
  }

  clearCart(): void {
    this.items.set([]);
    this.restaurantId.set(null);
    this.restaurantName.set(null);
    this.selectedPromo.set(null);
    localStorage.removeItem('qb_cart');
    localStorage.removeItem('qb_cart_rest_id');
    localStorage.removeItem('qb_cart_rest_name');
    localStorage.removeItem('qb_promo');
  }

  private save(): void {
    localStorage.setItem('qb_cart', JSON.stringify(this.items()));
    if (this.restaurantId()) localStorage.setItem('qb_cart_rest_id', String(this.restaurantId()));
    if (this.restaurantName()) localStorage.setItem('qb_cart_rest_name', this.restaurantName()!);
  }

  private loadStoredItems(): CartItem[] {
    try {
      const data = localStorage.getItem('qb_cart');
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  }

  private loadStoredRestaurantId(): number | null {
    const val = localStorage.getItem('qb_cart_rest_id');
    return val ? parseInt(val, 10) : null;
  }

  private loadStoredRestaurantName(): string | null {
    return localStorage.getItem('qb_cart_rest_name');
  }

  private loadStoredPromo(): PromoCode | null {
    try {
      const p = localStorage.getItem('qb_promo');
      return p ? JSON.parse(p) : null;
    } catch {
      return null;
    }
  }
}
