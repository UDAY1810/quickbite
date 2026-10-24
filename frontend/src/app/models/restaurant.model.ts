export interface MenuItem {
  id: number;
  name: string;
  description: string;
  price: number;
  category: string;
  available: boolean;
  imageUrl?: string;
  isVeg?: boolean;
  isBestseller?: boolean;
  rating?: number;
  calories?: number;
}

export interface Restaurant {
  id: number;
  name: string;
  description: string;
  address: string;
  cuisine: string;
  rating: number;
  open: boolean;
  imageUrl: string;
  menu?: MenuItem[];
  deliveryTime?: string;
  deliveryFee?: number;
  priceForTwo?: number;
  distance?: string;
  offers?: string[];
  featured?: boolean;
  tags?: string[];
}

export interface RestaurantRequest {
  name: string;
  description: string;
  address: string;
  cuisine: string;
  rating: number;
  open: boolean;
  imageUrl: string;
}

export interface MenuItemRequest {
  name: string;
  description: string;
  price: number;
  category: string;
  available: boolean;
  imageUrl?: string;
}
