import { Component, inject } from '@angular/core';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  template: `
    <div class="toast-container">
      @for (toast of toastService.toasts(); track toast.id) {
        <div class="toast-item toast-item--{{ toast.type }}" (click)="toastService.remove(toast.id)">
          <span class="toast-icon">{{ toast.icon }}</span>
          <span class="toast-text">{{ toast.text }}</span>
          <button class="toast-close" aria-label="Close">✕</button>
        </div>
      }
    </div>
  `,
  styles: [`
    .toast-container {
      position: fixed;
      bottom: 24px;
      right: 24px;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 10px;
      pointer-events: none;
      max-width: 400px;
    }
    .toast-item {
      pointer-events: auto;
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 18px;
      background: #0F1115;
      color: #fff;
      border-radius: 14px;
      box-shadow: 0 12px 36px rgba(0,0,0,0.28);
      font-family: 'Outfit', sans-serif;
      font-size: 14px;
      font-weight: 600;
      cursor: pointer;
      animation: toastSlideIn 0.35s cubic-bezier(0.16, 1, 0.3, 1) forwards;
      border: 1px solid rgba(255,255,255,0.12);
      backdrop-filter: blur(12px);
    }
    .toast-item--success {
      border-left: 4px solid #10B981;
    }
    .toast-item--info {
      border-left: 4px solid var(--qb-orange);
    }
    .toast-item--warning {
      border-left: 4px solid #F59E0B;
    }
    .toast-item--error {
      border-left: 4px solid #EF4444;
    }
    .toast-icon {
      font-size: 16px;
      color: var(--qb-orange-3);
    }
    .toast-text {
      flex: 1;
      line-height: 1.4;
    }
    .toast-close {
      background: none;
      border: none;
      color: rgba(255,255,255,0.4);
      cursor: pointer;
      font-size: 12px;
      padding: 2px;
      transition: color 0.15s;
    }
    .toast-close:hover {
      color: #fff;
    }
    @keyframes toastSlideIn {
      from {
        opacity: 0;
        transform: translateY(16px) scale(0.95);
      }
      to {
        opacity: 1;
        transform: translateY(0) scale(1);
      }
    }
    @media (max-width: 600px) {
      .toast-container {
        left: 16px;
        right: 16px;
        bottom: 16px;
        max-width: none;
      }
    }
  `]
})
export class ToastComponent {
  toastService = inject(ToastService);
}
