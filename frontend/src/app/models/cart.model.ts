import { MenuItem } from './restaurant.model';

export interface CartItem {
  menuItem: MenuItem;
  quantity: number;
  notes?: string;
}

export interface PromoCode {
  code: string;
  discountPercent?: number;
  flatDiscount?: number;
  minOrder: number;
  maxDiscount?: number;
  description: string;
}
