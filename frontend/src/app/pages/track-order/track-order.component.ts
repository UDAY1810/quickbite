import { Component, OnInit, OnDestroy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { OrderService } from '../../services/order.service';
import { TrackingService } from '../../services/tracking.service';
import { ToastService } from '../../services/toast.service';
import { OrderResponse, LiveTrackingUpdate } from '../../models/order.model';

@Component({
  selector: 'app-track-order',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './track-order.component.html',
  styleUrl: './track-order.component.css'
})
export class TrackOrderComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private orderService = inject(OrderService);
  private trackingService = inject(TrackingService);
  private toast = inject(ToastService);

  order: OrderResponse | null = null;
  loading = signal<boolean>(true);
  private updateSub: Subscription | null = null;

  currentStatus = 'CONFIRMED';
  currentStep = 2;
  currentLat = 12.9716;
  currentLng = 77.5946;
  etaMinutes = 20;

  driverName = 'Rajesh Kumar';
  driverRating = 4.9;
  vehicleNumber = 'KA-01-EA-4921';

  driverCoordinates = { x: 120, y: 145 };

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      const id = idParam ? parseInt(idParam, 10) : 3842;
      this.loadOrder(id);
    });

    this.updateSub = this.trackingService.updates$.subscribe(update => {
      this.handleLiveUpdate(update);
    });
  }

  private autoSimTimeout: any = null;

  ngOnDestroy(): void {
    if (this.autoSimTimeout) {
      clearTimeout(this.autoSimTimeout);
      this.autoSimTimeout = null;
    }
    this.updateSub?.unsubscribe();
    this.trackingService.disconnect();
  }

  loadOrder(id: number): void {
    this.loading.set(true);
    this.orderService.getOrder(id).subscribe({
      next: (order) => {
        this.order = order;
        this.currentStatus = order.status;
        this.driverName = order.driverName || 'Rajesh Kumar';
        this.driverRating = order.driverRating || 4.9;
        this.vehicleNumber = order.vehicleNumber || 'KA-01-EA-4921';
        this.currentStep = order.step || 2;
        this.etaMinutes = order.estimatedDeliveryMinutes || 20;
        this.updateDriverCoordinates(this.currentStep);
        this.loading.set(false);

        // Connect to real-time WebSocket for live updates from backend
        this.trackingService.connectToOrder(order.id);

        // If order is active and not delivered, auto-advance after 3.5s so user sees kitchen & delivery transitions
        if (this.currentStatus !== 'DELIVERED') {
          if (this.autoSimTimeout) clearTimeout(this.autoSimTimeout);
          this.autoSimTimeout = setTimeout(() => {
            if (this.currentStatus !== 'DELIVERED') {
              this.trackingService.startSimulation(order.id, this.currentStep);
            }
          }, 3500);
        }
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  handleLiveUpdate(update: LiveTrackingUpdate): void {
    this.currentStatus = update.status;
    if (update.driver) this.driverName = update.driver;
    if (update.lat) this.currentLat = update.lat;
    if (update.lng) this.currentLng = update.lng;

    if (update.step !== undefined) {
      this.currentStep = update.step;
      this.etaMinutes = Math.max(2, 22 - (update.step * 2));
      this.updateDriverCoordinates(update.step);
    }

    if (this.order) {
      this.orderService.updateOrderStatus(this.order.id, update.status, update.step, update.lat, update.lng);
    }
  }

  updateDriverCoordinates(step: number): void {
    // Map points:
    // P1: (120, 145) [Restaurant]
    // P2: (445, 145) [Corner 1]
    // P3: (445, 315) [Corner 2]
    // P4: (680, 315) [Customer Home]
    const clamped = Math.max(1, Math.min(10, step));
    const progress = (clamped - 1) / 9; // 0 to 1

    if (progress <= 0.45) {
      // Along horizontal top (120 -> 445)
      const subProg = progress / 0.45;
      this.driverCoordinates = {
        x: 120 + subProg * (445 - 120),
        y: 145
      };
    } else if (progress <= 0.7) {
      // Along vertical leg (145 -> 315)
      const subProg = (progress - 0.45) / 0.25;
      this.driverCoordinates = {
        x: 445,
        y: 145 + subProg * (315 - 145)
      };
    } else {
      // Along bottom horizontal leg (445 -> 680)
      const subProg = (progress - 0.7) / 0.3;
      this.driverCoordinates = {
        x: 445 + subProg * (680 - 445),
        y: 315
      };
    }
  }

  triggerSimulation(): void {
    if (!this.order) return;
    this.trackingService.startSimulation(this.order.id, 1);
  }

  callDriver(): void {
    this.toast.show(`Calling ${this.driverName} (+91 98765 43210)… 📞`, 'info');
  }

  messageDriver(): void {
    this.toast.show(`Driver chat window opened with ${this.driverName} 💬`, 'info');
  }
}
