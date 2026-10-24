export type OrderStatus =
  | 'PLACED'
  | 'CONFIRMED'
  | 'PREPARING'
  | 'OUT_FOR_DELIVERY'
  | 'DELIVERED'
  | 'CANCELLED';

export interface OrderItemRequest {
  menuItemId: number;
  quantity: number;
}

export interface PlaceOrderRequest {
  restaurantId: number;
  deliveryAddress: string;
  items: OrderItemRequest[];
}

export interface OrderItemResponse {
  menuItemId: number;
  itemName: string;
  unitPrice: number;
  quantity: number;
}

export interface OrderResponse {
  id: number;
  customerId: number;
  restaurantId: number;
  restaurantName?: string;
  status: OrderStatus | string;
  totalAmount: number;
  deliveryAddress: string;
  createdAt: string;
  items: OrderItemResponse[];
  driverName?: string;
  driverPhone?: string;
  driverRating?: number;
  vehicleNumber?: string;
  estimatedDeliveryMinutes?: number;
  currentLat?: number;
  currentLng?: number;
  step?: number;
}

export interface LiveTrackingUpdate {
  orderId: number;
  status: OrderStatus | string;
  driver?: string;
  lat?: number;
  lng?: number;
  step?: number;
  timestamp?: string;
}
