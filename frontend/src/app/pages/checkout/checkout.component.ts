import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';
import { RazorpayService } from '../../services/razorpay.service';
import { ToastService } from '../../services/toast.service';
import { AuthService } from '../../services/auth.service';
import { PlaceOrderRequest } from '../../models/order.model';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.css'
})
export class CheckoutComponent implements OnInit {
  cart = inject(CartService);
  private orderService = inject(OrderService);
  private razorpayService = inject(RazorpayService);
  private toast = inject(ToastService);
  private auth = inject(AuthService);
  private router = inject(Router);

  selectedAddressType: 'home' | 'work' | 'custom' = 'home';
  customAddress = '';
  selectedPaymentMethod: 'upi' | 'card' | 'wallet' | 'cod' = 'upi';
  couponInput = '';
  isPlacingOrder = false;

  ngOnInit(): void {
    this.customAddress = this.cart.deliveryAddress();
  }

  selectAddressType(type: 'home' | 'work', addr: string): void {
    this.selectedAddressType = type;
    this.customAddress = addr;
    this.cart.setAddress(addr);
  }

  applyCoupon(): void {
    if (!this.couponInput) return;
    this.cart.applyPromo(this.couponInput);
    this.couponInput = '';
  }

  placeOrder(): void {
    if (this.cart.items().length === 0) {
      this.toast.show('Your cart is empty', 'warning');
      return;
    }

    if (!this.cart.restaurantId()) {
      this.toast.show('No restaurant specified', 'warning');
      return;
    }

    this.isPlacingOrder = true;

    const req: PlaceOrderRequest = {
      restaurantId: this.cart.restaurantId()!,
      deliveryAddress: this.customAddress || this.cart.deliveryAddress(),
      items: this.cart.items().map(i => ({
        menuItemId: i.menuItem.id,
        quantity: i.quantity
      }))
    };

    // Step 1: Place order with Order Service
    this.orderService.placeOrder(req).subscribe({
      next: (order) => {
        // If Cash on Delivery, complete immediately
        if (this.selectedPaymentMethod === 'cod') {
          this.isPlacingOrder = false;
          this.toast.show(`Order #${order.id} placed! Pay cash on delivery 💵`, 'success');
          this.cart.clearCart();
          this.router.navigate(['/track', order.id]);
          return;
        }

        // Step 2: Online Payment via Razorpay
        this.toast.show('Opening Razorpay Secure Gateway… 🔒', 'info');

        const amountPaise = Math.round((order.totalAmount || this.cart.grandTotal()) * 100);
        this.razorpayService.createOrder(order.id, amountPaise).subscribe({
          next: (razorpayOrder) => {
            const user = this.auth.user();
            const customerInfo = {
              name: user?.fullName || 'QuickBite Foodie',
              email: 'customer@quickbite.com',
              phone: '9876543210'
            };

            // Step 3: Launch Razorpay Checkout Modal
            this.razorpayService.openRazorpayModal(
              razorpayOrder,
              customerInfo,
              (response) => {
                // Step 4: Verify Payment signature
                this.toast.show('Verifying payment with bank…', 'info');
                this.razorpayService.verifyPayment({
                  orderId: order.id,
                  razorpayOrderId: response.razorpay_order_id,
                  razorpayPaymentId: response.razorpay_payment_id,
                  razorpaySignature: response.razorpay_signature
                }).subscribe({
                  next: () => {
                    this.isPlacingOrder = false;
                    this.orderService.updateOrderStatus(order.id, 'CONFIRMED');
                    this.toast.show(`Payment Successful! Order #${order.id} confirmed 🎉`, 'success');
                    this.cart.clearCart();
                    this.router.navigate(['/track', order.id]);
                  },
                  error: () => {
                    this.isPlacingOrder = false;
                    this.toast.show('Payment verification failed. Please contact support.', 'error');
                  }
                });
              },
              () => {
                // Modal was closed by user
                this.isPlacingOrder = false;
                this.toast.show('Payment window closed. You can retry checkout anytime.', 'warning');
              }
            );
          },
          error: (err) => {
            this.isPlacingOrder = false;
            this.toast.show('Failed to initiate payment gateway. Please retry.', 'error');
          }
        });
      },
      error: (err) => {
        this.isPlacingOrder = false;
        this.toast.show('Failed to place order. Please try again.', 'error');
      }
    });
  }
}
