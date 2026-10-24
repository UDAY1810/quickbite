import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { CartService } from '../../services/cart.service';
import { ToastService } from '../../services/toast.service';
import { OrderResponse } from '../../models/order.model';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css'
})
export class OrdersComponent implements OnInit {
  private orderService = inject(OrderService);
  private cart = inject(CartService);
  private toast = inject(ToastService);

  orders: OrderResponse[] = [];
  loading = signal<boolean>(true);
  activeTab: 'all' | 'active' | 'delivered' = 'all';

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading.set(true);
    this.orderService.getMyOrders().subscribe({
      next: (list) => {
        this.orders = list;
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  get filteredOrders(): OrderResponse[] {
    if (this.activeTab === 'active') {
      return this.orders.filter(o => o.status !== 'DELIVERED' && o.status !== 'CANCELLED');
    }
    if (this.activeTab === 'delivered') {
      return this.orders.filter(o => o.status === 'DELIVERED');
    }
    return this.orders;
  }

  get activeOrdersCount(): number {
    return this.orders.filter(o => o.status !== 'DELIVERED' && o.status !== 'CANCELLED').length;
  }

  formatDate(isoString: string): string {
    try {
      const d = new Date(isoString);
      return d.toLocaleDateString('en-IN', {
        day: 'numeric',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return 'Recent';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'PLACED': return 'Order Placed';
      case 'CONFIRMED': return 'Confirmed';
      case 'PREPARING': return 'Cooking';
      case 'OUT_FOR_DELIVERY': return 'Out for Delivery';
      case 'DELIVERED': return 'Delivered';
      case 'CANCELLED': return 'Cancelled';
      default: return status;
    }
  }

  reorder(order: OrderResponse): void {
    this.cart.clearCart();
    order.items.forEach(item => {
      this.cart.addItem(
        {
          id: item.menuItemId,
          name: item.itemName,
          price: item.unitPrice,
          category: 'Mains',
          description: '',
          available: true
        },
        {
          id: order.restaurantId,
          name: order.restaurantName || 'QuickBite Kitchen'
        }
      );
    });

    this.toast.show(`Added ${order.items.length} items from past order to cart! 🛒`, 'success');
    this.cart.openDrawer();
  }
}
