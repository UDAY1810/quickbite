import { Injectable, signal } from '@angular/core';

export interface ToastMessage {
  id: number;
  text: string;
  type: 'success' | 'info' | 'warning' | 'error';
  icon?: string;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  toasts = signal<ToastMessage[]>([]);
  private counter = 0;

  show(text: string, type: 'success' | 'info' | 'warning' | 'error' = 'success', icon?: string): void {
    const id = ++this.counter;
    const defaultIcon = type === 'success' ? '✓' : type === 'error' ? '✕' : type === 'warning' ? '⚠' : 'ℹ';
    const newToast: ToastMessage = { id, text, type, icon: icon || defaultIcon };

    this.toasts.update(list => [...list, newToast]);

    setTimeout(() => {
      this.remove(id);
    }, 3800);
  }

  remove(id: number): void {
    this.toasts.update(list => list.filter(t => t.id !== id));
  }
}
