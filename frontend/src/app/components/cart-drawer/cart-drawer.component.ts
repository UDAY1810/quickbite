import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService, AVAILABLE_PROMOS } from '../../services/cart.service';

@Component({
  selector: 'app-cart-drawer',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    @if (cart.isDrawerOpen()) {
      <div class="cart-backdrop" (click)="cart.closeDrawer()"></div>
      <aside class="cart-drawer anim-slideLeft">
        <!-- Header -->
        <div class="cart-header">
          <div class="cart-header__info">
            <h2 class="cart-header__title">Your Order</h2>
            @if (cart.restaurantName()) {
              <p class="cart-header__sub">From <b>{{ cart.restaurantName() }}</b></p>
            }
          </div>
          <button class="cart-close" (click)="cart.closeDrawer()" aria-label="Close cart">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 6L6 18M6 6l12 12"/></svg>
          </button>
        </div>

        @if (cart.items().length === 0) {
          <!-- Empty State -->
          <div class="cart-empty">
            <div class="cart-empty__icon">🛍️</div>
            <h3>Your cart is empty</h3>
            <p>Good food is always just a few clicks away! Explore top menus near you.</p>
            <button class="qb-btn qb-btn--primary" (click)="cart.closeDrawer()" routerLink="/">
              Explore Restaurants
            </button>
          </div>
        } @else {
          <!-- Content with scroll -->
          <div class="cart-body">
            <!-- Delivery Destination banner -->
            <div class="cart-address-box">
              <span class="cart-address-box__icon">📍</span>
              <div class="cart-address-box__text">
                <span class="cart-address-box__label">Delivering to</span>
                <span class="cart-address-box__addr">{{ cart.deliveryAddress() }}</span>
              </div>
            </div>

            <!-- Items list -->
            <div class="cart-items">
              @for (item of cart.items(); track item.menuItem.id) {
                <div class="cart-item">
                  <div class="cart-item__img-wrap">
                    @if (item.menuItem.imageUrl) {
                      <img [src]="item.menuItem.imageUrl" [alt]="item.menuItem.name" class="cart-item__img" />
                    } @else {
                      <div class="cart-item__img-placeholder">🍽️</div>
                    }
                    <span class="veg-badge" [class.veg-badge--veg]="item.menuItem.isVeg" [class.veg-badge--nonveg]="!item.menuItem.isVeg">
                      <span class="veg-badge__circle"></span>
                    </span>
                  </div>

                  <div class="cart-item__details">
                    <h4 class="cart-item__title">{{ item.menuItem.name }}</h4>
                    <span class="cart-item__price">₹{{ item.menuItem.price * item.quantity }}</span>
                  </div>

                  <div class="cart-stepper">
                    <button class="cart-stepper__btn" (click)="cart.updateQuantity(item.menuItem.id, -1)">−</button>
                    <span class="cart-stepper__qty">{{ item.quantity }}</span>
                    <button class="cart-stepper__btn" (click)="cart.updateQuantity(item.menuItem.id, 1)">+</button>
                  </div>
                </div>
              }
            </div>

            <!-- Special Instructions -->
            <div class="cart-section">
              <label class="cart-section__label">Special instructions for kitchen</label>
              <input
                type="text"
                class="cart-input"
                placeholder="e.g. Extra spicy, no onions, cutlery please"
                [ngModel]="cart.notes()"
                (ngModelChange)="cart.notes.set($event)"
              />
            </div>

            <!-- Coupons Section -->
            <div class="cart-section">
              <div class="cart-promo-form">
                <input
                  type="text"
                  class="cart-input cart-promo-input"
                  placeholder="Enter Promo Code"
                  [(ngModel)]="couponInput"
                  (keyup.enter)="applyCoupon()"
                />
                <button class="cart-promo-apply-btn" (click)="applyCoupon()">Apply</button>
              </div>

              @if (cart.selectedPromo()) {
                <div class="cart-applied-promo">
                  <span>🎉 <b>{{ cart.selectedPromo()?.code }}</b> applied (-₹{{ cart.discountAmount() }})</span>
                  <button class="cart-applied-promo__remove" (click)="cart.removePromo()">Remove</button>
                </div>
              } @else {
                <div class="cart-promo-chips">
                  @for (p of availablePromos; track p.code) {
                    <button class="cart-promo-chip" (click)="quickApply(p.code)">
                      <span>🏷️ {{ p.code }}</span>
                      <small>Save {{ p.discountPercent ? p.discountPercent + '%' : '₹' + p.flatDiscount }}</small>
                    </button>
                  }
                </div>
              }
            </div>

            <!-- Bill Details -->
            <div class="cart-bill">
              <h4 class="cart-bill__title">Bill Details</h4>
              <div class="cart-bill__row">
                <span>Item Subtotal</span>
                <span>₹{{ cart.itemsSubtotal() }}</span>
              </div>
              <div class="cart-bill__row">
                <span>Delivery Partner Fee</span>
                @if (cart.deliveryFee() === 0) {
                  <span class="cart-free-badge">FREE</span>
                } @else {
                  <span>₹{{ cart.deliveryFee() }}</span>
                }
              </div>
              <div class="cart-bill__row">
                <span>Platform & Packaging</span>
                <span>₹{{ cart.platformFee() }}</span>
              </div>
              <div class="cart-bill__row">
                <span>Govt. Taxes (GST 5%)</span>
                <span>₹{{ cart.taxes() }}</span>
              </div>
              @if (cart.discountAmount() > 0) {
                <div class="cart-bill__row cart-bill__row--discount">
                  <span>Promo Discount</span>
                  <span>-₹{{ cart.discountAmount() }}</span>
                </div>
              }
              <div class="cart-bill__divider"></div>
              <div class="cart-bill__row cart-bill__row--total">
                <span>To Pay</span>
                <span>₹{{ cart.grandTotal() }}</span>
              </div>
            </div>
          </div>

          <!-- Footer Checkout Action -->
          <div class="cart-footer">
            <button class="qb-btn qb-btn--primary cart-checkout-btn" (click)="goToCheckout()">
              <span>Proceed to Checkout</span>
              <span class="cart-checkout-btn__price">₹{{ cart.grandTotal() }} →</span>
            </button>
          </div>
        }
      </aside>
    }
  `,
  styles: [`
    .cart-backdrop {
      position: fixed;
      inset: 0;
      background: rgba(15, 17, 21, 0.6);
      backdrop-filter: blur(6px);
      z-index: 1000;
      animation: fadeIn 0.25s ease-out;
    }
    .cart-drawer {
      position: fixed;
      top: 0;
      right: 0;
      bottom: 0;
      width: 440px;
      max-width: 90vw;
      background: #FFFFFF;
      z-index: 1001;
      display: flex;
      flex-direction: column;
      box-shadow: -10px 0 40px rgba(0,0,0,0.22);
      font-family: 'Inter', system-ui, sans-serif;
    }
    .anim-slideLeft {
      animation: slideLeft 0.32s cubic-bezier(0.16, 1, 0.3, 1) forwards;
    }
    .cart-header {
      padding: 20px 24px;
      border-bottom: 1px solid var(--qb-border);
      display: flex;
      align-items: center;
      justify-content: space-between;
      background: var(--qb-cream);
    }
    .cart-header__title {
      font-family: 'Outfit', sans-serif;
      font-size: 20px;
      font-weight: 800;
      color: var(--qb-dark);
      margin: 0;
    }
    .cart-header__sub {
      font-size: 13px;
      color: var(--qb-muted);
      margin-top: 2px;
    }
    .cart-close {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: #fff;
      border: 1px solid var(--qb-border);
      cursor: pointer;
      display: grid;
      place-items: center;
      color: var(--qb-dark);
      transition: all 0.15s;
    }
    .cart-close:hover {
      background: var(--qb-orange);
      color: #fff;
      border-color: var(--qb-orange);
    }
    .cart-empty {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      text-align: center;
      padding: 40px 32px;
    }
    .cart-empty__icon {
      font-size: 56px;
      margin-bottom: 16px;
      animation: floatY 2.8s ease-in-out infinite;
    }
    .cart-empty h3 {
      font-family: 'Outfit', sans-serif;
      font-size: 20px;
      font-weight: 700;
      margin-bottom: 8px;
    }
    .cart-empty p {
      font-size: 14px;
      color: var(--qb-muted);
      line-height: 1.5;
      margin-bottom: 24px;
      max-width: 280px;
    }
    .cart-body {
      flex: 1;
      overflow-y: auto;
      padding: 20px 24px;
      display: flex;
      flex-direction: column;
      gap: 20px;
    }
    .cart-address-box {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 16px;
      background: var(--qb-warm);
      border: 1px solid rgba(255, 59, 30, 0.16);
      border-radius: 14px;
    }
    .cart-address-box__icon {
      font-size: 20px;
    }
    .cart-address-box__label {
      display: block;
      font-size: 11px;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.06em;
      color: var(--qb-orange);
    }
    .cart-address-box__addr {
      font-size: 13px;
      font-weight: 600;
      color: var(--qb-dark);
      display: -webkit-box;
      -webkit-line-clamp: 1;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    .cart-items {
      display: flex;
      flex-direction: column;
      gap: 14px;
    }
    .cart-item {
      display: flex;
      align-items: center;
      gap: 14px;
      padding-bottom: 14px;
      border-bottom: 1px dashed var(--qb-border);
    }
    .cart-item__img-wrap {
      position: relative;
      width: 54px;
      height: 54px;
      flex-shrink: 0;
      border-radius: 10px;
      overflow: hidden;
    }
    .cart-item__img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    .cart-item__img-placeholder {
      width: 100%;
      height: 100%;
      display: grid;
      place-items: center;
      background: var(--qb-warm);
      font-size: 20px;
    }
    .veg-badge {
      position: absolute;
      top: 3px;
      left: 3px;
      width: 13px;
      height: 13px;
      border-radius: 3px;
      background: #fff;
      display: grid;
      place-items: center;
      border: 1.5px solid #10B981;
    }
    .veg-badge--nonveg {
      border-color: #EF4444;
    }
    .veg-badge__circle {
      width: 5px;
      height: 5px;
      border-radius: 50%;
      background: #10B981;
    }
    .veg-badge--nonveg .veg-badge__circle {
      background: #EF4444;
    }
    .cart-item__details {
      flex: 1;
      min-width: 0;
    }
    .cart-item__title {
      font-family: 'Outfit', sans-serif;
      font-size: 14px;
      font-weight: 700;
      color: var(--qb-dark);
      margin: 0 0 4px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    .cart-item__price {
      font-size: 13px;
      font-weight: 700;
      color: var(--qb-orange);
    }
    .cart-stepper {
      display: inline-flex;
      align-items: center;
      background: var(--qb-warm);
      border: 1px solid rgba(255, 59, 30, 0.2);
      border-radius: 999px;
      padding: 2px 6px;
      gap: 8px;
    }
    .cart-stepper__btn {
      background: none;
      border: none;
      cursor: pointer;
      font-family: 'Outfit', sans-serif;
      font-size: 16px;
      font-weight: 800;
      color: var(--qb-orange);
      width: 22px;
      height: 22px;
      display: grid;
      place-items: center;
      border-radius: 50%;
    }
    .cart-stepper__btn:hover {
      background: rgba(255, 59, 30, 0.12);
    }
    .cart-stepper__qty {
      font-family: 'Outfit', sans-serif;
      font-size: 13px;
      font-weight: 800;
      color: var(--qb-dark);
      min-width: 14px;
      text-align: center;
    }
    .cart-section {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .cart-section__label {
      font-family: 'Outfit', sans-serif;
      font-size: 12px;
      font-weight: 700;
      color: var(--qb-muted);
      text-transform: uppercase;
      letter-spacing: 0.06em;
    }
    .cart-input {
      width: 100%;
      padding: 10px 14px;
      border-radius: 10px;
      border: 1px solid var(--qb-border);
      font-size: 13px;
      font-family: inherit;
      outline: none;
      transition: border-color 0.15s;
    }
    .cart-input:focus {
      border-color: var(--qb-orange);
    }
    .cart-promo-form {
      display: flex;
      gap: 8px;
    }
    .cart-promo-input {
      text-transform: uppercase;
      font-weight: 700;
      letter-spacing: 0.05em;
    }
    .cart-promo-apply-btn {
      padding: 0 18px;
      border-radius: 10px;
      background: var(--qb-dark);
      color: #fff;
      border: none;
      font-family: 'Outfit', sans-serif;
      font-weight: 700;
      font-size: 13px;
      cursor: pointer;
      transition: background 0.15s;
    }
    .cart-promo-apply-btn:hover {
      background: var(--qb-orange);
    }
    .cart-applied-promo {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 8px 12px;
      background: #ECFDF5;
      border: 1px solid #A7F3D0;
      border-radius: 10px;
      font-size: 12px;
      color: #065F46;
    }
    .cart-applied-promo__remove {
      background: none;
      border: none;
      color: #DC2626;
      font-weight: 700;
      cursor: pointer;
    }
    .cart-promo-chips {
      display: flex;
      gap: 8px;
      overflow-x: auto;
      padding-bottom: 4px;
    }
    .cart-promo-chip {
      background: #fff;
      border: 1px dashed var(--qb-border);
      padding: 6px 10px;
      border-radius: 8px;
      cursor: pointer;
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      gap: 2px;
      white-space: nowrap;
      transition: all 0.15s;
    }
    .cart-promo-chip:hover {
      border-color: var(--qb-orange);
      background: var(--qb-warm);
    }
    .cart-promo-chip span {
      font-family: 'Outfit', sans-serif;
      font-size: 11px;
      font-weight: 800;
      color: var(--qb-dark);
    }
    .cart-promo-chip small {
      font-size: 10px;
      color: #10B981;
      font-weight: 700;
    }
    .cart-bill {
      background: var(--qb-cream);
      border-radius: 16px;
      padding: 16px;
      display: flex;
      flex-direction: column;
      gap: 10px;
      border: 1px solid var(--qb-border);
    }
    .cart-bill__title {
      font-family: 'Outfit', sans-serif;
      font-size: 14px;
      font-weight: 800;
      color: var(--qb-dark);
      margin: 0 0 4px;
    }
    .cart-bill__row {
      display: flex;
      justify-content: space-between;
      font-size: 13px;
      color: #4B5563;
    }
    .cart-bill__row--discount {
      color: #10B981;
      font-weight: 600;
    }
    .cart-free-badge {
      background: #D1FAE5;
      color: #065F46;
      padding: 1px 6px;
      border-radius: 4px;
      font-weight: 800;
      font-size: 11px;
    }
    .cart-bill__divider {
      height: 1px;
      background: var(--qb-border);
      margin: 4px 0;
    }
    .cart-bill__row--total {
      font-family: 'Outfit', sans-serif;
      font-size: 16px;
      font-weight: 800;
      color: var(--qb-dark);
    }
    .cart-footer {
      padding: 18px 24px;
      border-top: 1px solid var(--qb-border);
      background: #FFFFFF;
    }
    .cart-checkout-btn {
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 14px 22px;
      font-size: 15px;
    }
    .cart-checkout-btn__price {
      background: rgba(0,0,0,0.18);
      padding: 4px 10px;
      border-radius: 999px;
      font-weight: 800;
    }
    @keyframes fadeIn {
      from { opacity: 0; }
      to { opacity: 1; }
    }
    @keyframes slideLeft {
      from { transform: translateX(100%); }
      to { transform: translateX(0); }
    }
  `]
})
export class CartDrawerComponent {
  cart = inject(CartService);
  private router = inject(Router);

  couponInput = '';
  availablePromos = AVAILABLE_PROMOS;

  applyCoupon(): void {
    if (!this.couponInput) return;
    this.cart.applyPromo(this.couponInput);
    this.couponInput = '';
  }

  quickApply(code: string): void {
    this.cart.applyPromo(code);
  }

  goToCheckout(): void {
    this.cart.closeDrawer();
    this.router.navigate(['/checkout']);
  }
}
