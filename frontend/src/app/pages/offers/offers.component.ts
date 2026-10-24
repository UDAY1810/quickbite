import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart.service';
import { ToastService } from '../../services/toast.service';

interface DealItem {
  code: string;
  title: string;
  description: string;
  discountTag: string;
  minOrder: number;
  theme: 'orange' | 'green' | 'purple';
}

@Component({
  selector: 'app-offers',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './offers.component.html',
  styleUrl: './offers.component.css'
})
export class OffersComponent {
  private cart = inject(CartService);
  private toast = inject(ToastService);

  deals: DealItem[] = [
    {
      code: 'QUICK50',
      title: '50% OFF First Order',
      description: 'Get an incredible 50% discount up to ₹150 on your very first QuickBite experience.',
      discountTag: '50% OFF',
      minOrder: 199,
      theme: 'orange'
    },
    {
      code: 'BITE100',
      title: 'Flat ₹100 Gourmet Savings',
      description: 'Enjoy a flat ₹100 discount on your favorite top restaurants and pizzerias.',
      discountTag: 'FLAT ₹100',
      minOrder: 399,
      theme: 'green'
    },
    {
      code: 'FEAST20',
      title: '20% OFF Party Feasts',
      description: 'Host your friends and family with 20% savings up to ₹200 on all large orders.',
      discountTag: '20% OFF',
      minOrder: 499,
      theme: 'purple'
    },
    {
      code: 'FREESHIP',
      title: 'Free Delivery All Day',
      description: 'Zero delivery fee on any order above ₹149. Delivered piping hot.',
      discountTag: 'FREE DEL',
      minOrder: 149,
      theme: 'orange'
    }
  ];

  applyDeal(code: string): void {
    const res = this.cart.applyPromo(code);
    if (res.success) {
      this.cart.openDrawer();
    } else {
      this.toast.show(res.message, 'warning');
    }
  }
}
